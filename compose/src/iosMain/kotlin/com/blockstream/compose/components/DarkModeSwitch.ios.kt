package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual val darkModeAvailable: Boolean = false

actual fun darkModeOn(): Boolean = false

actual fun setDarkModeOn(on: Boolean) {}

actual fun Modifier.keepColorsInDark(): Modifier = this

@Composable
actual fun DarkModeWindow() {}

@Composable
actual fun DarkModeSelection(content: @Composable () -> Unit) = content()
