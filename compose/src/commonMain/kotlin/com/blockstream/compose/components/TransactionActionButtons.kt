package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_receive
import blockstream_green.common.generated.resources.id_send
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLineDown
import com.adamglin.phosphoricons.regular.ArrowLineUp
import com.blockstream.compose.GreenPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionActionButtons(
    modifier: Modifier = Modifier,
    isSendEnabled: Boolean,
    onSend: () -> Unit,
    onReceive: () -> Unit,
) {
    GreenRow(
        space = 8,
        padding = 0,
        modifier = modifier
    ) {
        // zeni: no Buy (a third-party shop) and no Swap. Send and Receive.
        GreenActionButton(
            text = Res.string.id_send,
            icon = PhosphorIcons.Regular.ArrowLineUp,
            enabled = isSendEnabled,
            onClick = onSend
        )

        GreenActionButton(
            text = Res.string.id_receive,
            icon = PhosphorIcons.Regular.ArrowLineDown,
            onClick = onReceive
        )

    }
}

@Composable
@Preview
fun TransactionActionButtonsPreview() {
    GreenPreview {
        TransactionActionButtons(
            isSendEnabled = true,
            onSend = { },
            onReceive = { }
        )
    }
}
