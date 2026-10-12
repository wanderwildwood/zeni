package com.blockstream.compose.nfc

import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
actual fun nfcShareAvailable(): Boolean {
    val context = LocalContext.current
    var available by remember { mutableStateOf(false) }
    // Looked at again on every resume: NFC may have been switched on in quick settings.
    LifecycleResumeEffect(context) {
        available = context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) &&
                NfcAdapter.getDefaultAdapter(context)?.isEnabled == true
        onPauseOrDispose { }
    }
    return available
}
