package com.blockstream.compose.screens.onboarding.hardware

import com.blockstream.compose.theme.displaySmall
import blockstream_green.common.generated.resources.id_connect_hardware_wallet
import com.blockstream.compose.components.NoPicture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.arrow_square_out
import blockstream_green.common.generated.resources.blockstream_devices
import blockstream_green.common.generated.resources.hardware_security
import blockstream_green.common.generated.resources.id_check_our_store
import blockstream_green.common.generated.resources.id_connect_a_different_hardware
import blockstream_green.common.generated.resources.id_connect_jade
import blockstream_green.common.generated.resources.id_dont_have_a_jade
import blockstream_green.common.generated.resources.id_fully_airgapped_with_qr
import blockstream_green.common.generated.resources.id_hardware_security
import blockstream_green.common.generated.resources.id_jade_is_a_specialized_device
import blockstream_green.common.generated.resources.id_jade_is_an_isolated_device_not
import blockstream_green.common.generated.resources.id_offline_key_storage
import blockstream_green.common.generated.resources.id_qr_mode_allows_you_to
import blockstream_green.common.generated.resources.id_welcome_to_blockstream_jade
import blockstream_green.common.generated.resources.id_your_bitcoin_and_liquid_assets
import blockstream_green.common.generated.resources.offline_key_storage
import blockstream_green.common.generated.resources.qr_airgapped
import com.blockstream.compose.models.onboarding.hardware.UseHardwareDeviceViewModel
import com.blockstream.compose.models.onboarding.hardware.UseHardwareDeviceViewModelAbstract
import com.blockstream.compose.components.GreenButton
import com.blockstream.compose.components.GreenButtonColor
import com.blockstream.compose.components.GreenButtonSize
import com.blockstream.compose.components.GreenButtonType
import com.blockstream.compose.theme.bodyLarge
import com.blockstream.compose.theme.displayMedium
import com.blockstream.compose.theme.green
import com.blockstream.compose.theme.labelLarge
import com.blockstream.compose.theme.md_theme_surfaceCircle
import com.blockstream.compose.theme.whiteMedium
import com.blockstream.compose.utils.SetupScreen
import com.blockstream.compose.utils.noRippleClickable
import com.blockstream.compose.components.GreenColumn
import com.blockstream.compose.components.GreenRow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UseHardwareDeviceScreen(
    viewModel: UseHardwareDeviceViewModelAbstract
) {

    SetupScreen(
        viewModel = viewModel,
        withPadding = false,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        // zeni: one heading, no four-page pitch or pictures; the buttons below do the work.
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.id_connect_hardware_wallet),
                style = displaySmall,
                textAlign = TextAlign.Center
            )
        }

        GreenColumn(
            padding = 24,
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            GreenButton(
                stringResource(Res.string.id_connect_jade),
                modifier = Modifier.fillMaxWidth(),
                size = GreenButtonSize.BIG,
            ) {
                viewModel.postEvent(UseHardwareDeviceViewModel.LocalEvents.ConnectJade)
            }

            GreenButton(
                stringResource(Res.string.id_connect_a_different_hardware),
                modifier = Modifier.fillMaxWidth(),
                size = GreenButtonSize.BIG,
                type = GreenButtonType.OUTLINE,
                color = GreenButtonColor.WHITE
            ) {
                viewModel.postEvent(UseHardwareDeviceViewModel.LocalEvents.ConnectDifferentHardwareDevice)
            }

            // zeni: no shop link.
        }
    }
}