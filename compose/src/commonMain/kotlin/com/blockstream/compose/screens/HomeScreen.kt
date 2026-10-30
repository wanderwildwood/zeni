package com.blockstream.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_by_using_blockstream_app_you_agree
import blockstream_green.common.generated.resources.id_connect_hardware_wallet
import blockstream_green.common.generated.resources.id_connect_jade
import blockstream_green.common.generated.resources.id_everything_you_need_to_take
import blockstream_green.common.generated.resources.id_get_started
import blockstream_green.common.generated.resources.id_green_is_now_the_blockstream_app
import blockstream_green.common.generated.resources.id_privacy_policy
import blockstream_green.common.generated.resources.id_simple__secure_selfcustody
import blockstream_green.common.generated.resources.id_terms_of_service
import blockstream_green.common.generated.resources.id_weve_redesigned_the_app_to_make_it_faster
import com.blockstream.compose.components.Banner
import com.blockstream.compose.components.GreenButton
import com.blockstream.compose.components.GreenButtonColor
import com.blockstream.compose.components.GreenButtonSize
import com.blockstream.compose.components.GreenButtonType
import com.blockstream.compose.components.GreenColumn
import com.blockstream.compose.events.Events
import com.blockstream.compose.extensions.linkText
import com.blockstream.compose.models.home.HomeViewModel
import com.blockstream.compose.models.home.HomeViewModelAbstract
import com.blockstream.compose.navigation.NavigateDestinations
import com.blockstream.compose.navigation.getResult
import com.blockstream.compose.theme.bodyLarge
import com.blockstream.compose.theme.bodyMedium
import com.blockstream.compose.theme.displayLarge
import com.blockstream.compose.theme.md_theme_background
import com.blockstream.compose.theme.titleLarge
import com.blockstream.compose.theme.whiteMedium
import com.blockstream.compose.utils.SetupScreen
import com.blockstream.compose.utils.appTestTag
import com.blockstream.compose.utils.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModelAbstract,
) {
    NavigateDestinations.Analytics.getResult<Boolean> {
        viewModel.postEvent(Events.Continue)
    }

    SetupScreen(
        viewModel = viewModel, withPadding = false, modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column {
                // zeni: no Blockstream logo.

                Banner(viewModel = viewModel, withTopPadding = true)

                val isEmptyWallet by viewModel.isEmptyWallet.collectAsStateWithLifecycle()

                when (isEmptyWallet) {
                    true -> {
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {

                            Column(modifier = Modifier.padding(vertical = 32.dp)) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(Res.string.id_simple__secure_selfcustody),
                                    style = displayLarge,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(Res.string.id_everything_you_need_to_take),
                                    textAlign = TextAlign.Center,
                                    color = whiteMedium
                                )
                            }

                            GreenColumn(
                                padding = 0, horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val onProgress by viewModel.onProgress.collectAsStateWithLifecycle()

                                GreenButton(
                                    stringResource(Res.string.id_get_started),
                                    modifier = Modifier.fillMaxWidth().appTestTag("get_started"),
                                    size = GreenButtonSize.BIG,
                                    enabled = !onProgress
                                ) {
                                    viewModel.postEvent(HomeViewModel.LocalEvents.GetStarted)
                                }

                                // zeni: for any hardware wallet, not only a Jade.
                                GreenButton(
                                    stringResource(Res.string.id_connect_hardware_wallet),
                                    modifier = Modifier.fillMaxWidth().appTestTag("connect_jade"),
                                    size = GreenButtonSize.BIG,
                                    type = GreenButtonType.OUTLINE,
                                    color = GreenButtonColor.GREEN,
                                    enabled = !onProgress
                                ) {
                                    viewModel.postEvent(HomeViewModel.LocalEvents.ConnectJade)
                                }

                                // zeni: no Blockstream terms line; this is not their app.
                            }
                        }

                    }

                    false -> {
                        WalletsScreen(
                            modifier = Modifier.weight(1f).padding(top = 8.dp),
                            viewModel = viewModel,
                        )
                    }

                    null -> {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

            }
        }
    }
}
