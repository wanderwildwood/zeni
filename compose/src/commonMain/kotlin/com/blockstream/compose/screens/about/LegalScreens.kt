package com.blockstream.compose.screens.about

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.zeni_based_on
import blockstream_green.common.generated.resources.zeni_built_with
import blockstream_green.common.generated.resources.zeni_licence
import blockstream_green.common.generated.resources.zeni_licence_body
import blockstream_green.common.generated.resources.zeni_no_browser
import blockstream_green.common.generated.resources.zeni_not_affiliated
import blockstream_green.common.generated.resources.zeni_page_next
import blockstream_green.common.generated.resources.zeni_page_number
import blockstream_green.common.generated.resources.zeni_page_previous
import blockstream_green.common.generated.resources.zeni_source_code
import com.blockstream.compose.models.about.LegalViewModel
import com.blockstream.compose.theme.bodyMedium
import com.blockstream.compose.theme.labelMedium
import com.blockstream.compose.theme.titleSmall
import com.blockstream.compose.utils.SetupScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * The first row of the next page: the row after the last one that fits whole. A row taller than
 * the viewport (it should not happen, rows are short) still moves on by one so the pager can
 * never stick.
 */
fun nextPageStart(firstVisible: Int, lastFullyVisible: Int?, lastVisible: Int, count: Int): Int {
    val last = lastFullyVisible ?: lastVisible
    return (if (last < firstVisible) firstVisible else last).plus(1).coerceAtMost(count)
}

/** The rows, a page at a time on whole rows: no touch scrolling, no scrollbar, no animation. */
@Composable
private fun PagedRows(
    rows: List<LegalRow>,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state: LazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // The first row of every page shown so far, so Previous can go back exactly.
    var starts by rememberSaveable { mutableStateOf(arrayListOf(0)) }

    // The page ends on the last row that fits whole: once the page is laid out at full height,
    // the list is cut to just those rows, so no row is ever shown half.
    var clipPx by remember { mutableStateOf<Int?>(null) }
    val density = LocalDensity.current

    LaunchedEffect(starts.size) {
        clipPx = null
        state.scrollToItem(starts.last())
        withFrameNanos { }
        withFrameNanos { }
        val info = state.layoutInfo
        val fully = info.visibleItemsInfo.lastOrNull { it.offset + it.size <= info.viewportEndOffset }
        if (fully != null && fully.index < rows.size - 1) clipPx = fully.offset + fully.size
    }

    Column(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(
                state = state,
                userScrollEnabled = false,
                modifier = (clipPx?.let { Modifier.height(with(density) { it.toDp() }) } ?: Modifier.fillMaxHeight())
                    .fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                itemsIndexed(rows) { _, row -> LegalRowView(row, onOpen) }
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PagerButton(stringResource(Res.string.zeni_page_previous), enabled = starts.size > 1) {
                starts = ArrayList(starts.dropLast(1))
            }
            Text(
                stringResource(Res.string.zeni_page_number, starts.size),
                style = bodyMedium,
                textAlign = TextAlign.Center
            )
            val moreAhead = state.layoutInfo.visibleItemsInfo.lastOrNull()?.let { it.index < rows.size - 1 } ?: (rows.size > 1)
            PagerButton(stringResource(Res.string.zeni_page_next), enabled = moreAhead) {
                scope.launch {
                    val info = state.layoutInfo
                    val viewportEnd = info.viewportEndOffset
                    val visible = info.visibleItemsInfo
                    if (visible.isEmpty()) return@launch
                    val fully = visible.lastOrNull { it.offset + it.size <= viewportEnd }?.index
                    val next = nextPageStart(visible.first().index, fully, visible.last().index, rows.size)
                    if (next < rows.size && next != starts.last()) starts = ArrayList(starts + next)
                }
            }
        }
    }
}

@Composable
private fun PagerButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .border(2.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f)
        )
    }
}

@Composable
private fun LegalRowView(row: LegalRow, onOpen: (String) -> Unit) {
    when (row.kind) {
        RowKind.HEADING -> Text(
            row.text,
            style = titleSmall,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp)
        )

        RowKind.BODY -> Text(
            row.text,
            style = bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
        )

        RowKind.LINK -> Text(
            row.text,
            style = bodyMedium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable { row.url?.let(onOpen) }
                .padding(vertical = 12.dp)
        )

        RowKind.COMPONENT -> Column(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable { row.url?.let(onOpen) }
                .padding(vertical = 6.dp)
        ) {
            Text(row.text, style = labelMedium)
            Text(row.detail ?: "", style = bodyMedium)
        }
    }
}

@Composable
private fun LegalPage(viewModel: LegalViewModel, rows: List<LegalRow>?) {
    val uriHandler = LocalUriHandler.current
    val noBrowser = stringResource(Res.string.zeni_no_browser)
    var notice by remember { mutableStateOf<String?>(null) }

    SetupScreen(viewModel = viewModel, withPadding = false) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (rows != null) {
                    PagedRows(rows, onOpen = {
                        // With no browser installed the person is told so, not crashed.
                        runCatching { uriHandler.openUri(it) }.onFailure { _ -> notice = noBrowser }
                    })
                }
            }
            notice?.let {
                Text(it, style = bodyMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun rememberRows(vararg keys: Any, build: suspend () -> List<LegalRow>): List<LegalRow>? {
    var rows by remember { mutableStateOf<List<LegalRow>?>(null) }
    LaunchedEffect(*keys) { rows = build() }
    return rows
}

private suspend fun readText(path: String): String = Res.readBytes(path).decodeToString()

@Composable
fun PrivacyScreen(viewModel: LegalViewModel) {
    val rows = rememberRows { legalRows(readText("files/privacy.md"), markdown = true) }
    LegalPage(viewModel, rows)
}

@Composable
fun TermsScreen(viewModel: LegalViewModel) {
    val rows = rememberRows { legalRows(readText("files/terms.md"), markdown = true) }
    LegalPage(viewModel, rows)
}

/**
 * Where the source is, which licence the app is under with its full text, who it is based on,
 * and what it is built with. Plain text that turns on whole rows, with no rules between parts.
 */
@Composable
fun LicencesScreen(viewModel: LegalViewModel) {
    val sourceHeading = stringResource(Res.string.zeni_source_code)
    val licenceHeading = stringResource(Res.string.zeni_licence)
    val licenceBody = stringResource(Res.string.zeni_licence_body)
    val basedOn = stringResource(Res.string.zeni_based_on)
    val notAffiliated = stringResource(Res.string.zeni_not_affiliated)
    val builtWith = stringResource(Res.string.zeni_built_with)

    val rows = rememberRows {
        buildList {
            add(LegalRow(sourceHeading, RowKind.HEADING))
            add(LegalRow(SOURCE_URL, RowKind.LINK, url = SOURCE_URL))
            add(LegalRow(basedOn))
            add(LegalRow(UPSTREAM_URL, RowKind.LINK, url = UPSTREAM_URL))
            add(LegalRow(notAffiliated))
            add(LegalRow(licenceHeading, RowKind.HEADING))
            add(LegalRow(licenceBody))
            addAll(legalRows(readText("files/gpl_3_0.txt")))
            add(LegalRow(builtWith, RowKind.HEADING))
            components.forEach { add(LegalRow(it.name, RowKind.COMPONENT, detail = it.licence, url = it.link)) }
            licenceTexts.forEach { t ->
                add(LegalRow(t.name, RowKind.HEADING))
                addAll(legalRows(readText(t.file)))
            }
        }
    }
    LegalPage(viewModel, rows)
}
