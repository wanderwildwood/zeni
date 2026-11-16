package com.blockstream.compose.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.mp.KoinPlatform

actual val darkModeAvailable: Boolean = true

actual fun darkModeOn(): Boolean = DarkMode.on

actual fun setDarkModeOn(on: Boolean) {
    runCatching { KoinPlatform.getKoin().get<Context>() }.getOrNull()?.let { DarkMode.set(it, on) }
}

actual fun Modifier.keepColorsInDark(): Modifier = keepColors()

@Composable
actual fun DarkModeWindow() = DarkWindow()

@Composable
actual fun DarkModeSelection(content: @Composable () -> Unit) = DarkSelection(content)
