package com.blockstream.compose.theme

import android.app.Activity
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import androidx.lifecycle.LifecycleOwner

// zeni: on e-ink every frame of a fade or slide is a ghost on the panel, so the whole
// composition runs with a motion scale of zero and animations land on their end state
// at once, whatever the phone's own animator setting is. Call before setContent.
@OptIn(InternalComposeUiApi::class)
fun <T> T.stillMotion() where T : Activity, T : LifecycleOwner {
    window.decorView.compositionContext = window.decorView.createLifecycleAwareWindowRecomposer(
        coroutineContext = object : MotionDurationScale {
            override val scaleFactor: Float = 0f
        },
        lifecycle = lifecycle,
    )
}
