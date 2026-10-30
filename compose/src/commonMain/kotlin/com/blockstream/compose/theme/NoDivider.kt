package com.blockstream.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The E Ink house style draws no separator lines. Screens keep calling HorizontalDivider; it takes
 * the same arguments as the Material one and leaves an empty gap where the line would have been.
 */
@Composable
@Suppress("UNUSED_PARAMETER")
fun HorizontalDivider(modifier: Modifier = Modifier, thickness: Dp = 1.dp, color: Color = Color.Unspecified) {
    Spacer(modifier)
}
