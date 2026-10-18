package com.blockstream.data.btcpricehistory.datasource

import com.blockstream.data.btcpricehistory.BitcoinPriceHistoryHttpClient
import com.blockstream.data.btcpricehistory.model.NetworkBitcoinPriceData
import com.blockstream.data.managers.SessionManager
import com.blockstream.data.managers.SettingsManager
import com.blockstream.network.NetworkResponse
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BitcoinPriceHistoryRemoteDataSource(
    private val client: BitcoinPriceHistoryHttpClient,
    private val settingsManager: SettingsManager,
    private val sessionManager: SessionManager
) {
    suspend fun getPriceHistory(currency: String): NetworkResponse<NetworkBitcoinPriceData> {
        val settings = settingsManager.appSettings

        // zeni: with Tor or a proxy on, the chart goes the same way as the wallet, through
        // GDK, instead of straight out over the app's own HTTP client.
        if (settings.tor || settings.proxyEnabled) {
            return throughGdk(currency)
        }

        return client.get<NetworkBitcoinPriceData>(BASE_URL) {
            url {
                parameters.append("currency", currency)
            }
        }
    }

    private suspend fun throughGdk(currency: String): NetworkResponse<NetworkBitcoinPriceData> = try {
        val response = sessionManager.httpRequestHandler.plainGetJson("$BASE_URL?currency=$currency").jsonObject
        val body = response["body"]
        if (body == null || response["error"]?.jsonPrimitive?.content?.isNotBlank() == true) {
            NetworkResponse.Error(0, response["error"]?.jsonPrimitive?.content ?: "No answer")
        } else {
            NetworkResponse.Success(json.decodeFromJsonElement(NetworkBitcoinPriceData.serializer(), body))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        NetworkResponse.Error(0, e.message ?: "No answer")
    }

    companion object {
        const val BASE_URL = "https://green-btc-chart.blockstream.com/api/v1/bitcoin/prices"

        private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    }
}
