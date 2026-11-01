package com.blockstream.compose.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_cancel
import blockstream_green.common.generated.resources.id_do_you_want_to_enable_tor_to
import blockstream_green.common.generated.resources.id_ok
import blockstream_green.common.generated.resources.id_warning
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import com.blockstream.compose.components.EInkDialog
import com.blockstream.compose.components.GreenButton
import com.blockstream.compose.components.GreenButtonType
import com.blockstream.compose.models.MainViewModel
import com.blockstream.compose.theme.titleSmall
import org.jetbrains.compose.resources.stringResource

@Composable
fun TorWarningDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val answer = { enable: Boolean ->
        viewModel.postEvent(MainViewModel.LocalEvents.TorWarningResponse(enable = enable))
        onDismiss()
    }
    EInkDialog(onDismiss = { answer(false) }) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(Res.string.id_warning),
                style = titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(Res.string.id_do_you_want_to_enable_tor_to),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                GreenButton(
                    text = stringResource(Res.string.id_cancel),
                    type = GreenButtonType.OUTLINE,
                    modifier = Modifier.weight(1f)
                ) { answer(false) }
                Spacer(Modifier.width(10.dp))
                GreenButton(
                    text = stringResource(Res.string.id_ok),
                    modifier = Modifier.weight(1f)
                ) { answer(true) }
            }
        }
    }
}
