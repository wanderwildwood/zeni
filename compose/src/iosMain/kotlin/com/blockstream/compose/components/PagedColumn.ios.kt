package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// No MMD here: a plain lazy column with the same arguments.
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
    LazyColumn(
        modifier = if (onPullAtTop == null) modifier else modifier.pullAtTop(state) { onPullAtTop() },
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}
