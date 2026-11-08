package com.blockstream.data.prices

import com.blockstream.network.AppHttpClient
import com.blockstream.network.NetworkResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private object Client : AppHttpClient()

    private val latestMutex = Mutex()
    private val historyMutex = Mutex()

    private var latest: Map<String, Double> = emptyMap()
    private var latestAt = 0L
    private val history = mutableMapOf<String, Pair<Long, List<Pair<Long, Double>>>>()

    private fun now() = Clock.System.now().toEpochMilliseconds()

    /** A rate as GDK wants it: a plain decimal string. */
    fun rateString(rate: Double): String = if (rate == kotlin.math.floor(rate)) rate.toLong().toString() else rate.toString()

    fun isSupported(currency: String?) = currency != null && currency.uppercase() in CURRENCIES

    /** The latest price of one bitcoin in [currency], or null if it isn't known. */
    suspend fun rate(currency: String?, via: (suspend (String) -> JsonElement)?): Double? {
        val cur = currency?.uppercase()?.takeIf { it in CURRENCIES } ?: return null
        return latestMutex.withLock {
            if (now() - latestAt > LATEST_FRESH_MS || latest[cur] == null) {
                fetch(LATEST, via)?.let { parseLatest(it) }?.takeIf { it.isNotEmpty() }?.also {
                    latest = it
                    latestAt = now()
                }
            }
            latest[cur]
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

    private suspend fun fetch(url: String, via: (suspend (String) -> JsonElement)?): JsonElement? =
        withTimeoutOrNull(TIMEOUT_MS) {
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
