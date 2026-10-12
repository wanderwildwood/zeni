package com.blockstream.green.glance

import android.content.Context
import android.os.Bundle
import com.blockstream.compose.glance.LockScreenPrice
import com.blockstream.data.managers.SettingsManager
import com.blockstream.green.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import org.koin.mp.KoinPlatform
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

/**
 * The bitcoin price for the lock screen, for those who want it: off until switched on in
 * Settings. Only the market price, from the same place as the chart on the wallet page;
 * never a balance, an address or anything else about a wallet.
 *
 * Nothing runs on a timer. The lock screen gets the last price at once, and when that is more
 * than half an hour old Glance asks for a new one ([call] "refresh") and waits for it, as it
 * does for Sky's weather.
 */
class PriceOnLockScreen : GlanceProvider() {

    override fun enabled(context: Context): Boolean = LockScreenPrice.on(context)

    override fun lines(context: Context): List<Line> {
        val saved = cache(context)
        if (System.currentTimeMillis() - saved.getLong(AT, 0L) > FRESH_MS) refresh(context.applicationContext)
        val price = saved.getString(PRICE, null) ?: return emptyList()
        return listOf(Line(text = saved.getString(TEXT, null) ?: return emptyList(), lead = price))
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (method != REFRESH || callingPackage != READER) return null
        val context = context?.applicationContext ?: return null
        if (!enabled(context) || System.currentTimeMillis() - cache(context).getLong(AT, 0L) <= FRESH_MS) return null
        val job = refresh(context)
        runBlocking { withTimeoutOrNull(WAIT_MS) { job.join() } }
        return null
    }

    @Synchronized
    private fun refresh(context: Context): Job {
        running?.takeIf { it.isActive }?.let { return it }
        return scope.launch {
            try {
                // With Tor or a proxy on, the wallet's own traffic is hidden; a plain fetch from
                // the lock screen would not be, so the price waits until the app is in the clear.
                val settings = KoinPlatform.getKoin().getOrNull<SettingsManager>()?.getApplicationSettings()
                if (settings == null || settings.tor || settings.proxyEnabled) return@launch
                val currency = LockScreenPrice.currency(context)
                val prices = fetch(currency)
                val now = prices.last()
                val dayAgo = prices.first()
                val format = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                    runCatching { this.currency = Currency.getInstance(currency.uppercase()) }
                    maximumFractionDigits = 0
                }
                val change = if (dayAgo > 0) (now - dayAgo) / dayAgo * 100 else 0.0
                val text = context.getString(
                    R.string.zeni_lock_price_change,
                    "${if (change >= 0) "+" else "−"}${"%.1f".format(abs(change))}%"
                )
                cache(context).edit()
                    .putString(PRICE, format.format(now))
                    .putString(TEXT, text)
                    .putLong(AT, System.currentTimeMillis())
                    .apply()
                changed(context)
            } catch (_: Exception) {
                // No answer this time: the last price stays, and the next look asks again.
            }
        }.also { running = it }
    }

    // The last day's prices, oldest first.
    private fun fetch(currency: String): List<Double> {
        val connection = URL("$PRICES?currency=${currency.lowercase()}").openConnection() as HttpURLConnection
        connection.connectTimeout = 15_000
        connection.readTimeout = 15_000
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val day = JSONObject(body).getJSONArray("prices_day")
            return (0 until day.length())
                .map { day.getJSONArray(it) }
                .filter { !it.isNull(1) }
                .sortedBy { it.getLong(0) }
                .map { it.getDouble(1) }
                .also { require(it.isNotEmpty()) }
        } finally {
            connection.disconnect()
        }
    }

    private fun cache(context: Context) = context.getSharedPreferences(LockScreenPrice.CACHE, Context.MODE_PRIVATE)

    companion object {
        private const val PRICES = "https://green-btc-chart.blockstream.com/api/v1/bitcoin/prices"
        private const val PRICE = "price"
        private const val TEXT = "text"
        private const val AT = "at"
        private const val FRESH_MS = 30 * 60 * 1000L
        private const val REFRESH = "refresh"
        private const val WAIT_MS = 20_000L
        private var running: Job? = null
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
}
