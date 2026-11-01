package com.blockstream.compose.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * One press in the E Ink look. On Android it is MMD's `ButtonMMD` (filled) or `OutlinedButtonMMD`
 * (2 dp black rim, 8 dp corner); on desktop and iOS, which have no MMD, the Material equivalents.
 * Callers add the 48 dp minimum themselves ([GreenButton] does).
 */
@Composable
internal expect fun MmdButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    filled: Boolean,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
)
