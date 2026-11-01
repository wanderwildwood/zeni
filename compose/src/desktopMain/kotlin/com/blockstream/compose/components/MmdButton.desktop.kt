package com.blockstream.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        Button(onClick = onClick, modifier = modifier, enabled = enabled, shape = MaterialTheme.shapes.small, contentPadding = contentPadding, content = content)
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled, shape = MaterialTheme.shapes.small, contentPadding = contentPadding, content = content)
    }
}
