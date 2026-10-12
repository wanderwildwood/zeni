package com.blockstream.compose.navigation.dialogs

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import com.blockstream.compose.theme.NoDialogDim
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.blockstream.compose.navigation.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile

enum class DialogState {
    OPEN, CONFIRMED, DISMISSED, CANCELLED;

    companion object {
        fun fromInt(index: Int) = entries.getOrNull(index) ?: OPEN
    }
}

private fun dialogKey(dialog: Dialog) = "DialogState_${dialog.uniqueId}"

fun Dialog.onDialogResult(
    scope: CoroutineScope,
    navigator: NavController,
    onResult: (DialogState) -> Unit
) {
    navigator.currentBackStackEntry?.savedStateHandle?.getStateFlow(
        dialogKey(this), DialogState.OPEN.ordinal
    )?.map {
        DialogState.fromInt(it)
    }?.transformWhile { state ->
        if (state != DialogState.OPEN) {
            emit(state)
            false
        } else {
            true
        }
    }?.onEach { state ->
        onResult(state)
    }?.launchIn(scope)
}

@Composable
fun GenericDialog(
    dialog: Dialog,
    navController: NavController
) {
    val onDismiss = { state: DialogState ->
        // Desktop has issues persisting enum values
        navController.previousBackStackEntry?.savedStateHandle?.set(
            key = dialogKey(dialog),
            value = state.ordinal
        )
        navController.popBackStack()
    }

    AlertDialog(
        // zeni: MMD dialog — white, a 2 dp black rim, 12 dp corners, no dim.
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
        title = dialog.title?.let {
            {
                Text(text = it)
            }
        },
        text = dialog.message?.let {
            {
                SelectionContainer {
                    Text(text = it)
                }
            }
        },
        onDismissRequest = {
            onDismiss(DialogState.CANCELLED)
        },
        confirmButton = {
                NoDialogDim()
            TextButton(
                onClick = {
                    onDismiss(DialogState.CONFIRMED)
                }
            ) {
                Text(
                    text = dialog.confirmButtonText ?: "OK"
                )
            }
        },
        dismissButton = {
            dialog.dismissButtonText?.let { title ->
                TextButton(onClick = { onDismiss(DialogState.DISMISSED) }) {
                    Text(text = title)
                }
            }
        }
    )
}