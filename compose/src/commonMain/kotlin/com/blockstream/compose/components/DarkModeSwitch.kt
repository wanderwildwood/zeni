package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// zeni: dark mode (white on black) is Android's DarkMode.kt; these let the shared screens reach it.
// Elsewhere there is none: the Settings row is hidden and nothing is kept.

expect val darkModeAvailable: Boolean

// Read in a composable, it redraws when the switch changes.
expect fun darkModeOn(): Boolean

expect fun setDarkModeOn(on: Boolean)

// Keeps a QR code's own colours in dark mode, so a scanner can still read it.
expect fun Modifier.keepColorsInDark(): Modifier

// At the top of a dialog's content, so its window is dark from its first frame.
@Composable
expect fun DarkModeWindow()
