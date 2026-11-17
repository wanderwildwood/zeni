package com.blockstream.data.gdk

import com.blockstream.data.gdk.data.Network
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * zeni: the Electrum servers used when nobody has typed one in App Settings. GDK takes one
 * server per network, so the backup is the app's: when the first doesn't answer GDK's ping on
 * connect, the session connects again to the next, and keeps it until that one fails to answer.
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
    private val lock = ReentrantLock()

    fun primary(networkId: String): String? = servers[networkId]?.firstOrNull()

    /** The default server in use for this network now, or null if the network has none. */
    fun current(networkId: String): String? = lock.withLock {
        servers[networkId]?.let { it.getOrNull(chosen[networkId] ?: 0) ?: it.first() }
    }

    /**
     * Moves to the next default server, back round to the first after the last, so a backup
     * that stops answering later (or was chosen only because the phone was offline) gives way
     * to the first again; false when the network has no other server at all.
     */
    fun advance(networkId: String): Boolean {
        val list = servers[networkId] ?: return false
        if (list.size < 2) return false
        lock.withLock { chosen[networkId] = ((chosen[networkId] ?: 0) + 1) % list.size }
        return true
    }

    fun reset() = lock.withLock { chosen.clear() }
}
