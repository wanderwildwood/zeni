package com.blockstream.green.utils

import com.blockstream.compose.managers.isRealBrowser
import com.blockstream.compose.managers.isWebUrl
import com.blockstream.compose.managers.noBrowserMessage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkOpenerTest {
    @Test
    fun aBrowserIsABrowser() {
        assertTrue(isRealBrowser("org.mozilla.firefox"))
        assertTrue(isRealBrowser("com.einkbro"))
    }

    @Test
    fun theWebViewShellIsNotOne() {
        assertFalse(isRealBrowser("org.chromium.webview_shell"))
        assertFalse(isRealBrowser("com.google.android.webview_shell"))
        assertFalse(isRealBrowser(null))
        assertFalse(isRealBrowser("android"))
    }

    @Test
    fun onlyWebAddressesAreGuarded() {
        assertTrue(isWebUrl("https://github.com/wanderwildwood/zeni"))
        assertTrue(isWebUrl("HTTP://example.org"))
        assertFalse(isWebUrl("mailto:email@wanderthe.dev"))
        assertFalse(isWebUrl("bitcoin:bc1q"))
    }

    @Test
    fun theNoBrowserMessageCarriesTheAddress() {
        assertTrue(noBrowserMessage("https://a.b/c").contains("https://a.b/c"))
    }
}
