package com.blockstream.compose.screens.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blockstream.compose.components.GreenRow

/**
 * A settings row in the house style (STYLE.md, Settings screens): plain and full width, no box
 * and no rule, set apart from its neighbors by space; the label over its value, and whatever
 * the row holds (a switch, a field) beside or under them. A row with [onClick] takes the press
 * across its whole width.
 */
@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    titleMaxLines: Int = 1,
    subtitleMaxLines: Int = 2,
    contentPadding: PaddingValues = SettingsRowPadding,
) {
    @Composable
    fun TitleSubtitle() {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = SettingsRowTitle,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { heading() }
            )
        }
        subtitle?.let {
            if (it.isNotBlank()) {
                Text(
                    text = it,
                    style = SettingsRowValue,
                    maxLines = subtitleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier)
            .padding(contentPadding)
            .alpha(if (enabled) 1f else 0.6f)
    ) {
        if (content != null) {
            Column(Modifier.fillMaxWidth()) {
                val hasTitleOrSubtitle = title.isNotBlank() || (subtitle?.isNotBlank() == true)
                if (hasTitleOrSubtitle) {
                    TitleSubtitle()
                    Spacer(Modifier.height(8.dp))
                }
                content()
            }
        } else {
            GreenRow(
                space = 16,
                padding = 0,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingContent != null) {
                    Box(Modifier.padding(end = 12.dp)) { leadingContent() }
                }
                Column(modifier = Modifier.weight(1f)) {
                    TitleSubtitle()
                }
                rightContent?.invoke()
            }
        }
    }
}

/** The space above and below a settings row's words, as in the other apps' settings. */
val SettingsRowPadding = PaddingValues(vertical = 14.dp)

/**
 * A settings row's label: the house row's 18 sp. Regular weight: MMD's Lato Medium reads as
 * zeni's regular cut, and zeni's "Medium" is its semi-bold, heavier than the other apps' rows.
 */
val SettingsRowTitle: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)

/** The line under a label, what the row is set to: the 16 sp body step, in plain black. */
val SettingsRowValue: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)

/** A heading over a group of settings rows: bold, black, no rule (STYLE.md). */
@Composable
fun SettingsHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 2.dp)
            .semantics { heading() }
    )
}
