@file:OptIn(ExperimentalFoundationApi::class)

package com.blockstream.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextAlign
import blockstream_green.common.generated.resources.id_rename
import blockstream_green.common.generated.resources.id_remove_wallet
import blockstream_green.common.generated.resources.id_remove_wallet_tap_again
import blockstream_green.common.generated.resources.id_wallet_actions
import com.adamglin.phosphoricons.regular.DotsThreeVertical
import com.blockstream.compose.components.MmdTextButton
import com.blockstream.compose.components.rememberArmedFlag
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.pencil_simple
import blockstream_green.common.generated.resources.trash
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.CaretRight
import com.blockstream.compose.components.GreenCard
import com.blockstream.compose.components.GreenRow
import com.blockstream.compose.components.SwipeAction
import com.blockstream.compose.components.SwipeableActionsContainer
import com.blockstream.compose.looks.wallet.WalletListLook
import com.blockstream.compose.theme.GreenColors
import com.blockstream.compose.theme.bodySmall
import com.blockstream.compose.theme.titleSmall
import com.blockstream.compose.theme.whiteLow
import com.blockstream.compose.theme.whiteMedium
import com.blockstream.compose.utils.appTestTag
import com.blockstream.data.data.GreenWallet

@Composable
private fun WalletListRow(
    title: String,
    subtitle: String,
    isWatchOnly: Boolean,
    isConnected: Boolean,
    onClick: () -> Unit = {},
) {
    GreenRow(
        padding = 0,
        space = 8,
        modifier = Modifier
            .appTestTag(title)
            .combinedClickable(onClick = {
                onClick.invoke()
            })
            .padding(horizontal = 12.dp)
            .height(70.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = subtitle, style = bodySmall, color = whiteLow)
        }
        Icon(
            imageVector = PhosphorIcons.Regular.CaretRight,
            contentDescription = null,
            tint = whiteMedium,
            modifier = Modifier.size(24.dp)
        )
    }
}

open class WalletListItemCallbacks(
    val onWalletClick: (wallet: GreenWallet) -> Unit,
    val onWalletDelete: ((wallet: GreenWallet) -> Unit),
    val onWalletRename: ((wallet: GreenWallet) -> Unit),
) {
    companion object {
        val Empty = WalletListItemCallbacks({}, {}, {})
    }
}

@Composable
fun WalletListItem(
    look: WalletListLook,
    callbacks: WalletListItemCallbacks = WalletListItemCallbacks.Empty,
    onSwipe: (String?) -> Unit,
    isSwiped: Boolean
) {
    // Rename and remove sit behind a ⋮ button, not a swipe: a drag is an animation, and a
    // destructive action asks with the armed-row pattern (tap, the words change, tap again). It
    // stays armed, with no timer, until the next other touch, Back or leaving the screen.
    val removeArmed = rememberArmedFlag(look.greenWallet.id)

    Column(modifier = Modifier.fillMaxWidth()) {
        GreenCard(padding = 0, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    WalletListRow(
                        title = look.title,
                        subtitle = look.subtitle,
                        isWatchOnly = look.isWatchOnly,
                        isConnected = look.isConnected,
                        onClick = { callbacks.onWalletClick(look.greenWallet) }
                    )
                }
                IconButton(
                    onClick = {
                        removeArmed.on = false
                        onSwipe(if (isSwiped) null else look.greenWallet.id)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .appTestTag("wallet_actions_${look.greenWallet.id}")
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Regular.DotsThreeVertical,
                        contentDescription = stringResource(Res.string.id_wallet_actions)
                    )
                }
            }
        }

        if (isSwiped) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MmdTextButton(
                    onClick = { callbacks.onWalletRename(look.greenWallet) },
                    modifier = Modifier.weight(1f).appTestTag("rename_wallet_${look.greenWallet.id}")
                ) {
                    Text(stringResource(Res.string.id_rename))
                }
                MmdTextButton(
                    onClick = {
                        if (removeArmed.on) {
                            removeArmed.on = false
                            callbacks.onWalletDelete(look.greenWallet)
                        } else {
                            removeArmed.on = true
                        }
                    },
                    modifier = Modifier.weight(1f).appTestTag("remove_wallet_${look.greenWallet.id}")
                ) {
                    Text(
                        stringResource(
                            if (removeArmed.on) Res.string.id_remove_wallet_tap_again else Res.string.id_remove_wallet
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
