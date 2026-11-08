package com.blockstream.data.btcpricehistory.datasource

import com.blockstream.data.btcpricehistory.model.NetworkBitcoinPriceData
import com.blockstream.data.managers.SessionManager
import com.blockstream.data.managers.SettingsManager
import com.blockstream.data.prices.MempoolPrices
import com.blockstream.network.NetworkResponse
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock

/**
 * zeni: the price chart comes from mempool.space's price history (hourly for the last
 * couple of years, weekly before that). With Tor or a proxy on, the request goes the same
 * way as the wallet, through GDK, instead of straight out over the app's own HTTP client.
 */
class BitcoinPriceHistoryRemoteDataSource(
    private val settingsManager: SettingsManager,
    private val sessionManager: SessionManager
) {
    suspend fun getPriceHistory(currency: String): NetworkResponse<NetworkBitcoinPriceData> = try {
        val settings = settingsManager.appSettings
        val via: (suspend (String) -> JsonElement)? = if (settings.tor || settings.proxyEnabled) {
            { url -> sessionManager.httpRequestHandler.plainGetJson(url) }
        } else null

        val prices = MempoolPrices.history(currency, via)
        if (prices.isNullOrEmpty()) {
            NetworkResponse.Error(0, "No answer")
        } else {
            NetworkResponse.Success(chartData(currency.uppercase(), prices))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        NetworkResponse.Error(0, e.message ?: "No answer")
    }

    companion object {
        private const val HOUR = 3_600_000L
        private const val DAY = 24 * HOUR

        /** Shapes mempool.space's history like the chart's own data: a day, a month, everything. */
        fun chartData(currency: String, prices: List<Pair<Long, Double>>, now: Long = Clock.System.now().toEpochMilliseconds()): NetworkBitcoinPriceData {
            fun rows(list: List<Pair<Long, Double>>) = list.map { listOf(it.first.toDouble(), it.second) }

            val day = prices.filter { it.first >= now - DAY - HOUR }
            val month = prices.filter { it.first >= now - 31 * DAY }
            // One point a day is plenty for a year or five on an e-ink screen.
            val full = prices.groupBy { it.first / DAY }.values.map { it.last() }

            return NetworkBitcoinPriceData(
                currency = currency,
                pricesDay = rows(day.ifEmpty { prices.takeLast(2) }),
                pricesMonth = rows(month),
                pricesFull = rows(full),
            )
        }
    }
}
