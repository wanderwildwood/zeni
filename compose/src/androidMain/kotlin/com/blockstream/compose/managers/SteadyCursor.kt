package com.blockstream.compose.managers

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue

/**
 * Compose 1.12 keeps its "blink the text cursor" switch out of reach of Kotlin code (it is internal
 * there), though the getter is still public in the compiled library. This reads it by name and turns
 * the blink off, so a text field's cursor stays lit instead of redrawing the E Ink panel twice a
 * second. If the library ever drops it, the cursor simply keeps blinking and nothing breaks.
 */
@Suppress("UNCHECKED_CAST")
fun steadyCursor(): ProvidedValue<*>? = runCatching {
    val local = Class.forName("androidx.compose.ui.platform.CompositionLocalsKt")
        .getMethod("getLocalCursorBlinkEnabled")
        .invoke(null) as ProvidableCompositionLocal<Boolean>
    local provides false
}.getOrNull()
