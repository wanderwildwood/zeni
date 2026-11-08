package com.blockstream.data

import com.blockstream.data.btcpricehistory.datasource.BitcoinPriceHistoryRemoteDataSource
import com.blockstream.data.gdk.ZeniElectrum
import com.blockstream.data.gdk.data.Network
import com.blockstream.data.jade.PinServer
import com.blockstream.data.prices.MempoolPrices
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ZeniNetworkTest {

    @AfterTest
    fun reset() = ZeniElectrum.reset()

    @Test
    fun electrum_defaults_are_not_the_old_vendor() {
        assertEquals("electrum.jhoenicke.de:50002", ZeniElectrum.current(Network.ElectrumMainnet))
        ZeniElectrum.servers.values.flatten().forEach { assertFalse(it.contains("blockstream")) }
        ZeniElectrum.explorers.values.forEach { assertTrue(it.startsWith("https://mempool.space/")) }
    }

    @Test
    fun electrum_backup_is_taken_once() {
        assertTrue(ZeniElectrum.advance(Network.ElectrumMainnet))
        assertEquals("fulcrum.grey.pw:51002", ZeniElectrum.current(Network.ElectrumMainnet))
        assertFalse(ZeniElectrum.advance(Network.ElectrumMainnet))
        assertEquals("fulcrum.grey.pw:51002", ZeniElectrum.current(Network.ElectrumMainnet))
        assertNull(ZeniElectrum.current(Network.GreenMainnet))
        assertEquals("blackie.c3-soft.com:57006", ZeniElectrum.current(Network.ElectrumTestnet))
        assertFalse(ZeniElectrum.advance(Network.ElectrumTestnet))
    }

    @Test
    fun latest_prices_parse() {
        val json = Json.parseToJsonElement("""{"time":1794150005,"USD":75942,"EUR":68181,"GBP":56874,"CAD":107375,"CHF":62406,"AUD":110552,"JPY":12044113}""")
        val prices = MempoolPrices.parseLatest(json)
        assertEquals(75942.0, prices["USD"])
        assertEquals(7, prices.size)
        assertFalse("time" in prices)
    }

    @Test
    fun history_parses_oldest_first_in_millis() {
        val json = Json.parseToJsonElement(
            """{"prices":[{"time":1794150000,"EUR":68179,"USD":75942},{"time":1794146400,"EUR":68130,"USD":75848},{"time":1279497600,"USD":0.08}],"exchangeRates":{}}"""
        )
        val eur = MempoolPrices.parseHistory(json, "EUR")
        assertEquals(listOf(1794146400000L to 68130.0, 1794150000000L to 68179.0), eur)
        assertEquals(3, MempoolPrices.parseHistory(json, "USD").size)
    }

    @Test
    fun chart_data_has_a_day_a_month_and_everything() {
        val hour = 3_600_000L
        val now = 1_794_150_000_000L
        val prices = (0 until 24 * 60).map { now - it * hour to (70000.0 + it) }.reversed()
        val data = BitcoinPriceHistoryRemoteDataSource.chartData("USD", prices, now)
        assertEquals("USD", data.currency)
        assertEquals(26, data.dailyPrices.size) // 25 hours back, inclusive
        assertTrue(data.monthlyPrices.size in 24 * 30..24 * 32)
        assertTrue(data.fullPrices.size in 60..61) // one a day
        assertEquals(70000.0, data.dailyPrices.last()[1])
    }

    @Test
    fun pin_server_host_shown_is_the_clearnet_one() {
        assertEquals("j8d.io", PinServer.displayHost(listOf("https://j8d.io/get_pin", "http://mrrxtq6tjpbnbm7vh5jt6mpjctn7ggyfy5wegvbeff3x7jrznqawlmid.onion/get_pin")))
        assertEquals("pins.example.org", PinServer.displayHost(listOf("", "https://pins.example.org:8443/start_handshake")))
        assertEquals("abc.onion", PinServer.displayHost(listOf("http://abc.onion/get_pin")))
        assertNull(PinServer.displayHost(emptyList()))
    }
}
