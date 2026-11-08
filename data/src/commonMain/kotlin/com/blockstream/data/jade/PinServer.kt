package com.blockstream.data.jade

import com.blockstream.data.utils.hostname

/**
 * zeni: which PIN server (blind oracle) a hardware wallet used. The device names it in its
 * unlock request; the app never learns it any other way, so the last one seen is kept per
 * device and shown in Device info.
 */
object PinServer {
    /** The host to show for the URLs a device sent: its clearnet one if it has one, else its onion. */
    fun displayHost(urls: List<String>?): String? {
        val hosts = urls.orEmpty().filter { it.isNotBlank() }.map { it.hostname(excludePort = true) }.filter { it.isNotBlank() }
        return hosts.firstOrNull { !it.endsWith(".onion", ignoreCase = true) } ?: hosts.firstOrNull()
    }

    /** True for the URLs of an unlock (or setup) exchange with a PIN server. */
    fun isPinServerRequest(onReply: String?) = onReply == "pin" || onReply == "handshake_init" || onReply == "handshake_complete"
}
