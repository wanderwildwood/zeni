package com.blockstream.compose.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.node.Ref

/**
 * Wrapper around [AnimatedVisibility] that remembers the last state so that a nullable
 * state value can be used. Otherwise only the animate in works and the animate out is just
 * a quick flash.
 *
 * https://stackoverflow.com/a/77316971/914358
 */
@Composable
inline fun <T> AnimatedNullableVisibility(
    value: T?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = EnterTransition.None,
    exit: ExitTransition = ExitTransition.None,
    crossinline content: @Composable (T) -> Unit
) {
    val ref = remember {
        Ref<T>()
    }

    ref.value = value ?: ref.value

    AnimatedVisibility(
        modifier = modifier,
        visible = value != null,
        enter = enter,
        exit = exit,
        content = {
            ref.value?.let { value ->
                content(value)
            }
        }
    )
}

// The E Ink panel redraws in full: a pulse or a spin is a smear and a battery cost. These keep their
// names so callers read as before, and draw their content still.
@Composable
@Suppress("UNUSED_PARAMETER")
fun Pulsating(pulseFraction: Float = 0.95f, content: @Composable () -> Unit) {
    Box { content() }
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun AlphaPulse(pulseFraction: Float = 0.75f, duration: Int = 500, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) { content() }
}

@Composable
@Suppress("UNUSED_PARAMETER")
fun Rotating(duration: Int = 1000, enabled: Boolean = true, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) { content() }
}
