package com.blockstream.data.prices

import com.blockstream.network.AppHttpClient
import com.blockstream.network.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.time.Clock

/**
 * zeni: the bitcoin price, from mempool.space, for the fiat value beside amounts and for the
 * price chart. The wallet library's own price request (to the old vendor's price server) is
 * never made: GdkSession.convert hands GDK this rate instead.
 *
 * [via] carries a request through Tor or the proxy when either is on (GDK's HTTP client);
 * null means a plain request.
 */
object MempoolPrices {
    const val HOST = "mempool.space"
    const val LATEST = "https://mempool.space/api/v1/prices"
    const val HISTORY = "https://mempool.space/api/v1/historical-price"

    /** The currencies mempool.space prices bitcoin in. */
    val CURRENCIES = listOf("USD", "EUR", "GBP", "CAD", "CHF", "AUD", "JPY")

    private const val LATEST_FRESH_MS = 5 * 60 * 1000L
    private const val HISTORY_FRESH_MS = 30 * 60 * 1000L
    private const val TIMEOUT_MS = 10_000L
    /** After a failed fetch, how long before trying again: until then the cached rate answers at once. */
    private const val RETRY_AFTER_MS = 60_000L
    /** The oldest a rate is used at all: an older one gives no fiat value rather than a wrong one. */
    private const val STALE_MS = 60 * 60 * 1000L

    /** How long a request may go on in the background after its callers stopped waiting. */
    private const val BACKGROUND_TIMEOUT_MS = 2 * 60 * 1000L

    private object Client : AppHttpClient()

    /**
     * Requests run here, not in the caller: over Tor or the proxy the first one waits for GDK
     * to connect, which no cancellation reaches, so a caller waits out [TIMEOUT_MS] at most
     * and the request goes on without it.
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val latestMutex = Mutex()
    private val historyMutex = Mutex()

    private var latest: Map<String, Double> = emptyMap()
    private var latestAt = 0L
    private var latestFailedAt = 0L
    /** The latest-prices request under way, shared by everyone asking meanwhile. */
    private var latestJob: Deferred<Unit>? = null
    private val history = mutableMapOf<String, Pair<Long, List<Pair<Long, Double>>>>()

    private fun now() = Clock.System.now().toEpochMilliseconds()

    /** A rate as GDK wants it: a plain decimal string. */
    fun rateString(rate: Double): String = if (rate == kotlin.math.floor(rate)) rate.toLong().toString() else rate.toString()

    fun isSupported(currency: String?) = currency != null && currency.uppercase() in CURRENCIES

    /** The latest price of one bitcoin in [currency], or null if it isn't known. */
    suspend fun rate(currency: String?, via: (suspend (String) -> JsonElement)?): Double? {
        val cur = currency?.uppercase()?.takeIf { it in CURRENCIES } ?: return null
        // The lock is held only to read and write the cache, never across a request: every
        // amount on screen asks for the rate, and none waits behind a slow one in turn.
        val job = latestMutex.withLock {
            val due = now() - latestAt > LATEST_FRESH_MS || latest[cur] == null
            // A server that just failed is not asked again for a minute.
            if (due && now() - latestFailedAt > RETRY_AFTER_MS) {
                latestJob ?: scope.async { fetchLatest(via) }.also { latestJob = it }
            } else {
                null
            }
        }
        if (job != null && withTimeoutOrNull(TIMEOUT_MS) { job.await() } == null) {
            // Not answered in time: amounts go on without it, and for a minute no one waits
            // again. The request itself goes on and fills the cache if it gets an answer.
            latestMutex.withLock {
                if (latestJob === job) latestFailedAt = now()
            }
        }
        return latestMutex.withLock {
            latest[cur]?.takeIf { now() - latestAt <= STALE_MS }
        }
    }

    private suspend fun fetchLatest(via: (suspend (String) -> JsonElement)?) {
        val self = currentCoroutineContext()[Job]
        var fresh: Map<String, Double>? = null
        try {
            fresh = withTimeoutOrNull(BACKGROUND_TIMEOUT_MS) { request(LATEST, via) }
                ?.let { parseLatest(it) }?.takeIf { it.isNotEmpty() }
        } finally {
            withContext(NonCancellable) {
                latestMutex.withLock {
                    // A request from before a reset says nothing.
                    if (latestJob !== self) return@withLock
                    val answer = fresh
                    if (answer != null) {
                        latest = answer
                        latestAt = now()
                    } else {
                        latestFailedAt = now()
                    }
                    latestJob = null
                }
            }
        }
    }

    /** Hourly (recent) to weekly (old) prices, oldest first: epoch millis to price. */
    suspend fun history(currency: String, via: (suspend (String) -> JsonElement)?): List<Pair<Long, Double>>? {
        val cur = currency.uppercase().takeIf { it in CURRENCIES } ?: return null
        return historyMutex.withLock {
            history[cur]?.takeIf { now() - it.first < HISTORY_FRESH_MS }?.second
                ?: fetch("$HISTORY?currency=$cur", via)?.let { parseHistory(it, cur) }?.takeIf { it.isNotEmpty() }?.also {
                    history[cur] = now() to it
                }
                ?: history[cur]?.second
        }
    }

    /** One request, waited for [TIMEOUT_MS] at most; it goes on in the background after that. */
    private suspend fun fetch(url: String, via: (suspend (String) -> JsonElement)?): JsonElement? {
        val job = scope.async { withTimeoutOrNull(BACKGROUND_TIMEOUT_MS) { request(url, via) } }
        return withTimeoutOrNull(TIMEOUT_MS) { job.await() }
    }

    private suspend fun request(url: String, via: (suspend (String) -> JsonElement)?): JsonElement? =
        try {
            if (via != null) {
                // GDK's http_request answers {"body": …} or {"error": …}
                via(url).jsonObject["body"]
            } else {
                (Client.get<JsonElement>(url) as? NetworkResponse.Success)?.data
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            null
        }

    /** Forgets every price and request, for tests. */
    internal fun reset() {
        latest = emptyMap()
        latestAt = 0L
        latestFailedAt = 0L
        latestJob = null
        history.clear()
    }

    internal fun parseLatest(json: JsonElement): Map<String, Double> =
        (json as? JsonObject)?.let { obj ->
            CURRENCIES.mapNotNull { cur -> obj[cur]?.jsonPrimitive?.doubleOrNull?.takeIf { it > 0 }?.let { cur to it } }.toMap()
        } ?: emptyMap()

    internal fun parseHistory(json: JsonElement, currency: String): List<Pair<Long, Double>> =
        ((json as? JsonObject)?.get("prices")?.jsonArray ?: return emptyList()).mapNotNull { row ->
            val obj = row as? JsonObject ?: return@mapNotNull null
            val time = obj["time"]?.jsonPrimitive?.longOrNull ?: return@mapNotNull null
            val price = obj[currency]?.jsonPrimitive?.doubleOrNull?.takeIf { it > 0 } ?: return@mapNotNull null
            time * 1000 to price
        }.sortedBy { it.first }
}
