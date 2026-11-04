package com.blockstream.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.blockstream.compose.theme.NoDialogDim

/**
 * The dialog: a white panel with a 2 dp black rim, no dimmed backdrop and no animation (the
 * activity runs with motion scaled to zero). The dim is a screenful of dithered grey on E Ink.
 * Always the same width, whatever the buttons say. Lists inside run out into its 14 dp side
 * padding ([LocalListBleed]) so their scrollbar sits near the rim.
 */
@Composable
fun EInkDialog(
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit,
) {
    // Always the full house width, whatever the caller's properties say about it.
    val fixed = DialogProperties(
        dismissOnBackPress = properties.dismissOnBackPress,
        dismissOnClickOutside = properties.dismissOnClickOutside,
        usePlatformDefaultWidth = false,
    )
    Dialog(onDismissRequest = onDismiss, properties = fixed) {
        NoDialogDim()
        Surface(
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
        ) {
            // A dialog is its own window: its armed rows need a scope of their own.
            ArmedScope(Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 18.dp)) {
                    CompositionLocalProvider(LocalListBleed provides 14.dp) { content() }
                }
            }
        }
    }
}
