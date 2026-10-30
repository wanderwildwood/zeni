package com.blockstream.data

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlsTest {
    private val all = listOf(
        Urls.TERMS_OF_SERVICE, Urls.PRIVACY_POLICY, Urls.HELP_MNEMONIC_NOT_WORKING, Urls.HELP_RECEIVE_ASSETS,
        Urls.HELP_MNEMONIC_BACKUP, Urls.HELP_NLOCKTIMES, Urls.HELP_FEES, Urls.HELP_FUNDING_FEES,
        Urls.HELP_RECEIVE_CAPACITY, Urls.HELP_GET_LIQUID, Urls.HELP_JADE_USB_UPGRADE, Urls.HELP_WHATS_COLLECTED,
        Urls.HELP_BIP39_PASSPHRASE, Urls.HELP_LIGHTNING_SHORTCUT, Urls.HELP_LIGHTNING_BETA, Urls.HELP_JADE_AIRGAPPED,
        Urls.HELP_AMP_ASSETS, Urls.HELP_QR_PIN_UNLOCK, Urls.HELP_MASTER_BLINDING_KEY, Urls.HELP_2FA_PROTECTION,
        Urls.HELP_JADE_EXPORT_XPUB, Urls.HELP_HW_EXPORT_XPUB, Urls.HELP_CENTER, Urls.JADE_TROUBLESHOOT,
        Urls.HELP_JADE_SWAP, Urls.HELP_PERFORM_SWAP, Urls.HELP_SWAPS_UNAVAILABLE, Urls.LEDGER_SUPPORTED_ASSETS,
        Urls.SECURITY_WATCH_ONLY, Urls.STATUS_PAGE, Urls.APP_PAGE,
    )

    @Test
    fun noLinkLeadsToABlockstreamPage() {
        all.forEach {
            assertFalse(it.contains("blockstream", true), it)
            assertFalse(it.contains("blkstrm", true), it)
        }
    }

    @Test
    fun everyLinkIsHttpsOnTheForksOwnRepository() {
        all.forEach { assertTrue(it.startsWith("https://github.com/wanderwildwood/zeni"), it) }
    }
}
