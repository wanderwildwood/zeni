package com.blockstream.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mudita.mmd.components.buttons.ButtonMMD
import com.mudita.mmd.components.buttons.OutlinedButtonMMD

@Composable
internal actual fun MmdButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    filled: Boolean,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    if (filled) {
        ButtonMMD(onClick = onClick, modifier = modifier, enabled = enabled, contentPadding = contentPadding, content = content)
    } else {
        OutlinedButtonMMD(onClick = onClick, modifier = modifier, enabled = enabled, contentPadding = contentPadding, content = content)
    }
}
