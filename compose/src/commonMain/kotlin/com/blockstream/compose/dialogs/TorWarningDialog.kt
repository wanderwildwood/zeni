package com.blockstream.compose.dialogs

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import com.blockstream.compose.theme.NoDialogDim
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_cancel
import blockstream_green.common.generated.resources.id_do_you_want_to_enable_tor_to
import blockstream_green.common.generated.resources.id_ok
import blockstream_green.common.generated.resources.id_warning
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Warning
import com.blockstream.compose.models.MainViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun TorWarningDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        // zeni: MMD dialog — white, a 2 dp black rim, 12 dp corners, no dim.
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        onDismissRequest = {
            viewModel.postEvent(
                MainViewModel.LocalEvents.TorWarningResponse(
                    enable = false
                )
            )
            onDismiss()
        },
        icon = {
            Icon(imageVector = PhosphorIcons.Regular.Warning, contentDescription = null)
        },
        title = {
            Text(text = stringResource(Res.string.id_warning))
        },
        text = {
            Text(text = stringResource(Res.string.id_do_you_want_to_enable_tor_to))
        },
        confirmButton = {
                NoDialogDim()
            TextButton(
                onClick = {
                    viewModel.postEvent(
                        MainViewModel.LocalEvents.TorWarningResponse(
                            enable = true
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(Res.string.id_ok)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.postEvent(
                        MainViewModel.LocalEvents.TorWarningResponse(
                            enable = false
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(Res.string.id_cancel)
                )
            }
        }
    )
}
