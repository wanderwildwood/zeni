package com.blockstream.compose.theme

// zeni: the e-ink palette. Every name upstream uses is kept so the screens compile unchanged,
// but each one is now black or white, after the house Monochrome.kt (MMD's eInkColorScheme).
// There is no grey because the panel has none worth using, and no accent colour at all:
// emphasis is weight and rules, never hue.

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val md_theme_primary = Color.Black
val md_theme_onPrimary = Color.White
val md_theme_primaryContainer = Color.Black
val md_theme_onPrimaryContainer = Color.White

val md_theme_secondary = Color.White
val md_theme_onSecondary = Color.Black
val md_theme_secondaryContainer = Color.Black
val md_theme_onSecondaryContainer = Color.White

val md_theme_tertiary = Color.White
val md_theme_onTertiary = Color.Black
val md_theme_tertiaryContainer = Color.Black
val md_theme_onTertiaryContainer = Color.White

val md_theme_error = Color.Black
val md_theme_errorContainer = Color.White
val md_theme_onError = Color.White
val md_theme_onErrorContainer = Color.Black

val md_theme_background = Color.White
val md_theme_backgroundVariant = Color.White
val md_theme_onBackground = Color.Black

val md_theme_surface = Color.White // card, text fields
val md_theme_onSurface = Color.Black
val md_theme_surfaceVariant = Color.White
val md_theme_onSurfaceVariant = Color.Black
val md_theme_dialogSurface = Color.White // modal/dialog container, one tier lighter than surface

val md_theme_outline = Color.Black
val md_theme_inverseOnSurface = Color.Black
val md_theme_inverseSurface = Color.White
val md_theme_inversePrimary = Color.White
val md_theme_shadow = Color.Black
val md_theme_surfaceTint = Color.White // Make it the same as surface https://stackoverflow.com/a/76160786/914358
val md_theme_outlineVariant = Color.Black
val md_theme_scrim = Color.Black

val md_theme_brandSurface = Color.White

val bottom_nav_bg = Color.White

val green = md_theme_primary
val green20 = Color.White

val red = Color.Black
val redDark = Color.Black
val orange = Color.Black

val orangeSurface = Color.White
val orangeOutline = Color.Black

val blueSurface = Color.White
val blueOutline = Color.Black

val md_theme_surfaceCircle = Color.White

val whiteHigh
    @Composable
    get() = MaterialTheme.colorScheme.onSurface
val whiteMedium
    @Composable
    get() = MaterialTheme.colorScheme.onSurface
val whiteLow
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

val bitcoin = Color.Black
val liquid = Color.Black
val lightning = Color.Black
val bitcoin_testnet = Color.Black
val liquid_testnet = Color.Black
val amp = Color.Black
val amp_testnet = Color.Black


