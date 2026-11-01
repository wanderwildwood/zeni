package com.blockstream.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blockstream.compose.theme.titleSmall

/**
 * The question dialog: an optional [icon], a [title], a [message], and one or two house buttons
 * side by side (cancel left, confirm right, equal width, 10 dp apart). Replaces Material's
 * `AlertDialog`, which animates in and sizes itself to its buttons.
 */
@Composable
fun EInkAlertDialog(
    onDismissRequest: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    icon: (@Composable () -> Unit)? = null,
    dismissText: String? = null,
    onDismissClick: () -> Unit = {},
) {
    EInkDialog(onDismiss = onDismissRequest) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon?.invoke()
            title?.also {
                Text(text = it, style = titleSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            message?.also {
                SelectionContainer {
                    Text(text = it, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                if (dismissText != null) {
                    GreenButton(
                        text = dismissText,
                        type = GreenButtonType.OUTLINE,
                        modifier = Modifier.weight(1f),
                        onClick = onDismissClick
                    )
                    Spacer(Modifier.width(10.dp))
                }
                GreenButton(text = confirmText, modifier = Modifier.weight(1f), onClick = onConfirm)
            }
        }
    }
}
