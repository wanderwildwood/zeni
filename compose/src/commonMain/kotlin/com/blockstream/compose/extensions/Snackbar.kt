package com.blockstream.compose.extensions

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_contact_support
import com.blockstream.data.SupportType
import com.blockstream.data.data.SupportData
import com.blockstream.compose.managers.PlatformManager
import com.blockstream.compose.models.GreenViewModel
import com.blockstream.compose.navigation.NavigateDestinations
import com.blockstream.compose.sideeffects.DialogState
import com.blockstream.compose.utils.getStringFromIdOrNull
import org.jetbrains.compose.resources.getString

/**
 * zeni: which screen a notice went up on, so that a screen change closes only a notice from the
 * screen being left. A view model often posts a notice together with a step back ("Transaction
 * sent", "Wrong choice..."): the step runs first and the notice goes up after it, on the new
 * screen, so it must outlive that screen change. GreenApp points [currentEntryId] at its
 * NavController.
 */
object NoticeScreen {
    var currentEntryId: () -> String? = { null }

    var shownOn: String? = null
        private set

    fun markShown() {
        shownOn = currentEntryId()
    }

    /** True when the screen is now [entryId] and the notice showing went up on another one. */
    fun leftBehind(entryId: String?): Boolean = noticeLeftBehind(shownOn = shownOn, nowOn = entryId)
}

internal fun noticeLeftBehind(shownOn: String?, nowOn: String?): Boolean = shownOn != nowOn

/**
 * zeni: a notice stays until it is closed with its X or the screen changes (GreenApp), not for
 * four seconds: on an e-ink panel a message on a timer can be gone before it has been read. A
 * new one takes the place of the one showing rather than queuing behind it.
 */
suspend fun SnackbarHostState.showNotice(message: String, actionLabel: String? = null): SnackbarResult {
    currentSnackbarData?.dismiss()
    NoticeScreen.markShown()
    return showSnackbar(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = true,
        duration = SnackbarDuration.Indefinite
    )
}

suspend fun SnackbarHostState.showErrorSnackbar(
    platformManager: PlatformManager,
    dialogState: DialogState,
    viewModel: GreenViewModel,
    error: Throwable,
    supportData: SupportData? = null
) {
    val result = showNotice(
        message = getStringFromIdOrNull(error.message) ?: "",
        actionLabel = if (supportData != null) getString(Res.string.id_contact_support) else null
    )

    if (result == SnackbarResult.ActionPerformed && supportData != null) {
        viewModel.postEvent(
            NavigateDestinations.Support(
                type = SupportType.INCIDENT,
                supportData = supportData,
                greenWalletOrNull = viewModel.greenWalletOrNull
            )
        )
    }
}