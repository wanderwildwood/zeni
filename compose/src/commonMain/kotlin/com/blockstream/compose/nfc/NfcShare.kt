package com.blockstream.compose.nfc

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.flow.MutableStateFlow

// zeni: tap-to-pay. While a Receive screen is in front, this phone answers an NFC reader as a
// tag holding the payment request (a bitcoin: URI). Another phone that taps it opens Send with
// the address and amount filled in; nothing is ever sent without the payer confirming.
object NfcShare {
    val uri = MutableStateFlow<String?>(null)
}

// Offers [uri] over NFC only while the calling screen is resumed, so a backgrounded or
// locked app never hands out an address.
@Composable
fun NfcShareWhileShown(uri: String?) {
    LifecycleResumeEffect(uri) {
        NfcShare.uri.value = uri?.takeIf { it.isNotBlank() }
        onPauseOrDispose {
            if (NfcShare.uri.value == uri) NfcShare.uri.value = null
        }
    }
}

// True when this device can be tapped (NFC present, switched on, and able to act as a tag).
@Composable
expect fun nfcShareAvailable(): Boolean
