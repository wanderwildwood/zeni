package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.CaretDown
import com.adamglin.phosphoricons.regular.CaretUp
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A screen's content column that pages the E Ink way: a [PagedColumn] holding one item (the whole
 * column), so what used to be a `Column(Modifier.verticalScroll(...))` gets MMD's scrollbar and
 * turns by page. Side padding goes in [contentPadding] (default none: the parent screen pads) so the
 * scrollbar reaches the screen's edge; [modifier] is for size only (weight, fillMaxSize).
 */
@Composable
fun GreenPagedColumn(
    modifier: Modifier = Modifier,
    space: Int = 16,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    // MMD's scrollbar counts rows, and this list has one (the whole column), so it would draw
    // nothing a person could turn a page with. When the column is taller than the screen a slim
    // rail of two buttons turns a page at a time instead (instant, no animation).
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var viewport by remember { mutableStateOf(0) }
    val scrollable = state.canScrollBackward || state.canScrollForward

    Row(modifier = modifier) {
        PagedColumn(
            modifier = Modifier.weight(1f).fillMaxHeight().onSizeChanged { viewport = it.height },
            state = state,
            isScrollbarVisible = false,
            contentPadding = contentPadding,
        ) {
            item {
                Column(
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    content = content
                )
            }
        }
        if (scrollable) {
            Column(
                modifier = Modifier.fillMaxHeight().padding(end = 4.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                PageTurnButton(PhosphorIcons.Regular.CaretUp, enabled = state.canScrollBackward) {
                    scope.launch { state.scrollBy(-pageStep(viewport)) }
                }
                PageTurnButton(PhosphorIcons.Regular.CaretDown, enabled = state.canScrollForward) {
                    scope.launch { state.scrollBy(pageStep(viewport)) }
                }
            }
        }
    }
}

/** A page is the viewport less a strip of overlap, so the line you stopped at is still in view. */
fun pageStep(viewportPx: Int, overlapFraction: Float = 0.1f): Float =
    (viewportPx * (1f - overlapFraction)).coerceAtLeast(1f)

@Composable
private fun PageTurnButton(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    MmdButton(
        onClick = onClick,
        modifier = Modifier.size(width = 36.dp, height = 56.dp).padding(vertical = 4.dp),
        enabled = enabled,
        filled = false,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}
