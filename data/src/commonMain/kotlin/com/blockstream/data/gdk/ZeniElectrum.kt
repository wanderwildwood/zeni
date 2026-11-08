package com.blockstream.data.gdk

import com.blockstream.data.gdk.data.Network

/**
 * zeni: the Electrum servers used when nobody has typed one in App Settings. GDK takes one
 * server per network, so the backup is the app's: when the first doesn't answer GDK's ping on
 * connect, the session connects again to the next, and keeps it until the app is restarted.
 * A server typed in App Settings always wins.
 */
object ZeniElectrum {
    val servers: Map<String, List<String>> = mapOf(
        Network.ElectrumMainnet to listOf("electrum.jhoenicke.de:50002", "fulcrum.grey.pw:51002"),
        Network.ElectrumTestnet to listOf("blackie.c3-soft.com:57006"),
    )

    /** mempool.space's explorer, for "View in explorer" and an address's page. */
    val explorers: Map<String, String> = mapOf(
        Network.ElectrumMainnet to "https://mempool.space/",
        Network.ElectrumTestnet to "https://mempool.space/testnet/",
    )

    private val chosen = mutableMapOf<String, Int>()

    fun primary(networkId: String): String? = servers[networkId]?.firstOrNull()

    /** The default server in use for this network now, or null if the network has none. */
    fun current(networkId: String): String? = servers[networkId]?.let { it.getOrNull(chosen[networkId] ?: 0) ?: it.first() }

    /** Moves to the next default server; false when there is no other to try. */
    fun advance(networkId: String): Boolean {
        val list = servers[networkId] ?: return false
        val next = (chosen[networkId] ?: 0) + 1
        if (next >= list.size) return false
        chosen[networkId] = next
        return true
    }

    fun reset() = chosen.clear()
}
