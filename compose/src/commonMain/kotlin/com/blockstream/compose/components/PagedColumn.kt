package com.blockstream.compose.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * The list every screen uses. On Android it is Mudita's own `LazyColumnMMD` with its scrollbar (the
 * chevrons turn a page, the track jumps, the thumb shows where you are); on desktop and iOS, which
 * have no MMD, it is a plain lazy column. Same arguments as the shop's shared PagedColumn.
 *
 * A full-screen list keeps its side padding in [contentPadding] (start 20 dp, end 12 dp), never
 * on the list as a whole, so the scrollbar sits at the screen's edge. [onPullAtTop], when given,
 * is called when a swipe down starts on the first page: the list asks for its rows again.
 */
@Composable
expect fun PagedColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    isScrollbarVisible: Boolean = true,
    onPullAtTop: (() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
)

/** How far a list inside a dialog may run out into the dialog's padding; zero elsewhere. */
val LocalListBleed = compositionLocalOf { 0.dp }

/** The side padding of a full-screen list, so the scrollbar reaches the screen's edge. */
val PagedListPadding = PaddingValues(start = 20.dp, end = 12.dp)

/**
 * Calls [onPull] when a swipe that began with the list on its first page ends further down than
 * it began. It watches touches before the list sees them and takes none of them.
 */
internal fun Modifier.pullAtTop(state: LazyListState, onPull: () -> Unit): Modifier = pointerInput(state) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        val atTop = state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0
        var last = down.position.y
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            last = change.position.y
            if (!change.pressed) break
        }
        if (swipeTurn(last - down.position.y, atTop, canPull = true) == SwipeTurn.Pull) onPull()
    }
}

/** What one swipe does to the list. */
internal enum class SwipeTurn { Forward, Back, Pull, None }

/** Up turns a page on, down turns one back, down on the first page ([atTop]) pulls if [canPull]. */
internal fun swipeTurn(drag: Float, atTop: Boolean, canPull: Boolean): SwipeTurn = when {
    drag < 0f -> SwipeTurn.Forward
    drag == 0f -> SwipeTurn.None
    !atTop -> SwipeTurn.Back
    canPull -> SwipeTurn.Pull
    else -> SwipeTurn.None
}

