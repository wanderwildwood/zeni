package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
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
    // The shared list ends a whole-row list in a blank tail. When its rows arrive after the first
    // frame (Settings waits for the wallet's settings), the list keeps its place on that tail, as a
    // lazy list keeps its first item by key, and the page stays blank. Then go back to the top.
    if (!showCutRow) {
        LaunchedEffect(state) {
            snapshotFlow {
                val info = state.layoutInfo
                info.totalItemsCount > 1 && info.visibleItemsInfo.isNotEmpty() &&
                    info.visibleItemsInfo.all { it.key == SHARED_TAIL }
            }.collect { onlyTail -> if (onlyTail) state.scrollToItem(0) }
        }
    }
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

/** The key of the shared list's blank tail (paged/PagedColumn.kt, `TAIL`). */
private const val SHARED_TAIL = "paged-column-tail"
