package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/** The armed-row rule (house style): a tap arms, a second tap within [MS] does it, silence disarms. */
object ArmWindow {
    const val MS = 6000L

    fun isArmed(armedAtMs: Long?, nowMs: Long): Boolean =
        armedAtMs != null && nowMs - armedAtMs in 0 until MS

    /** What a press does now: arm, or (already armed and still inside the window) fire. */
    fun press(armedAtMs: Long?, nowMs: Long): Press =
        if (isArmed(armedAtMs, nowMs)) Press.Fire else Press.Arm

    enum class Press { Arm, Fire }
}

/** State of one armed button; disarms itself after [ArmWindow.MS]. */
class ArmedState internal constructor(initial: Boolean) {
    var armed by mutableStateOf(initial)
        internal set
}

@Composable
fun rememberArmed(): ArmedState {
    val state = remember { ArmedState(false) }
    LaunchedEffect(state.armed) {
        if (state.armed) {
            delay(ArmWindow.MS)
            state.armed = false
        }
    }
    return state
}
