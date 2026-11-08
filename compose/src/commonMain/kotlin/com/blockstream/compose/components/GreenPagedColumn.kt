package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A screen's content column that pages the E Ink way: a [PagedColumn] holding one item (the whole
 * column), so what used to be a `Column(Modifier.verticalScroll(...))` gets MMD's scrollbar and
 * turns by page. The column is one row taller than the page, which PagedColumn reads a page at a
 * time; its chevrons turn the pages, as in every other list (house style: no floating arrows).
 * Side padding goes in [contentPadding] (default none: the parent screen pads) so the scrollbar
 * reaches the screen's edge; [modifier] is for size only (weight, fillMaxSize).
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
    PagedColumn(
        modifier = modifier,
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
}
