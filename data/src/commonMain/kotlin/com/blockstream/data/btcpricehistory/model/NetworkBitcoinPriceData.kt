package com.blockstream.data.btcpricehistory.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Price rows of [epoch millis, price], for the last day, the last month, and all time. */
@Serializable
data class NetworkBitcoinPriceData(
    @SerialName("currency")
    val currency: String,
    @SerialName("last_refresh")
    val lastRefresh: String = "",
    @SerialName("prices_day")
    private val pricesDay: List<List<Double?>> = emptyList(),
    @SerialName("prices_month")
    private val pricesMonth: List<List<Double?>> = emptyList(),
    @SerialName("prices_full")
    private val pricesFull: List<List<Double?>> = emptyList(),
) {
    val dailyPrices: List<List<Double?>>
        get() = pricesDay.sortedBy { it[0] }

    val monthlyPrices: List<List<Double?>>
        get() = pricesMonth.sortedBy { it[0] }

    val fullPrices: List<List<Double?>>
        get() = pricesFull.sortedBy { it[0] }
}
