package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mudita.mmd.components.lazy.LazyColumnMMD

@Composable
actual fun PagedColumn(
    modifier: Modifier,
    state: LazyListState,
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    isScrollbarVisible: Boolean,
    onPullAtTop: (() -> Unit)?,
    content: LazyListScope.() -> Unit,
) {
    val pull by rememberUpdatedState(onPullAtTop)
    // In a dialog the list runs out into the dialog's padding so the scrollbar sits at the rim,
    // and takes the same amount off its own end so the rows stay where they were.
    val direction = LocalLayoutDirection.current
    val bleed = if (isScrollbarVisible) LocalListBleed.current else 0.dp
    val padding = if (bleed > 0.dp) {
        PaddingValues(
            start = contentPadding.calculateStartPadding(direction),
            top = contentPadding.calculateTopPadding(),
            end = contentPadding.calculateEndPadding(direction) + bleed,
            bottom = contentPadding.calculateBottomPadding(),
        )
    } else {
        contentPadding
    }
    val widen = if (bleed > 0.dp) {
        Modifier.layout { measurable, constraints ->
            val max = if (constraints.hasBoundedWidth) constraints.maxWidth + bleed.roundToPx() else constraints.maxWidth
            val placeable = measurable.measure(
                constraints.copy(minWidth = if (constraints.hasBoundedWidth) max else constraints.minWidth, maxWidth = max),
            )
            layout(placeable.width, placeable.height) { placeable.place(bleed.roundToPx() / 2, 0) }
        }
    } else {
        Modifier
    }
    LazyColumnMMD(
        modifier = (if (onPullAtTop == null) modifier else modifier.pullAtTop(state) { pull?.invoke(); Unit }).then(widen),
        state = state,
        contentPadding = padding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        isScrollbarVisible = isScrollbarVisible,
        content = content,
    )
}
