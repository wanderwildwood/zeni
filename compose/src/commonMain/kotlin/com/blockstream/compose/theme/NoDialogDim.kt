package com.blockstream.compose.theme

import androidx.compose.runtime.Composable

// zeni: call inside a dialog's content. The stock dim is a screenful of dithered grey on
// e-ink that repaints everything behind the dialog; MMD draws a white panel with a black rim.
@Composable
expect fun NoDialogDim()
