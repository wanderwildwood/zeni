package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.blockstream.compose.components.paged.LocalListBleed as SharedListBleed
import com.blockstream.compose.components.paged.PagedColumn as SharedPagedColumn

/**
 * The shop's shared PagedColumn (paged/PagedColumn.kt, copied whole from shared-pagedcolumn): pages
 * end on whole rows (3 Nov 2026), a tall first row is read a page at a time, and a content list
 * shows the row the foot cuts through ([showCutRow], 7 Nov 2026). This app's dialogs say how far a
 * list may run into their padding with the common [LocalListBleed]; it is handed on to the shared
 * list's own.
 */
@Composable
actual fun PagedColumn(
    modifier: Modifier,
    state: LazyListState,
    contentPadding: PaddingValues,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    isScrollbarVisible: Boolean,
    onPullAtTop: (() -> Unit)?,
    showCutRow: Boolean,
    content: LazyListScope.() -> Unit,
) {
    CompositionLocalProvider(SharedListBleed provides LocalListBleed.current) {
        SharedPagedColumn(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            isScrollbarVisible = isScrollbarVisible,
            onPullAtTop = onPullAtTop,
            showCutRow = showCutRow,
            content = content,
        )
    }
}
