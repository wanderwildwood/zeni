package com.blockstream.compose.screens.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_version
import blockstream_green.common.generated.resources.zeni_app_name
import blockstream_green.common.generated.resources.zeni_based_on
import blockstream_green.common.generated.resources.zeni_contact
import blockstream_green.common.generated.resources.zeni_contact_detail
import blockstream_green.common.generated.resources.zeni_contact_subject
import blockstream_green.common.generated.resources.zeni_licences
import blockstream_green.common.generated.resources.zeni_not_affiliated
import blockstream_green.common.generated.resources.zeni_privacy
import blockstream_green.common.generated.resources.zeni_terms
import com.blockstream.compose.GreenPreview
import com.blockstream.compose.models.about.AboutViewModel
import com.blockstream.compose.models.about.AboutViewModelAbstract
import com.blockstream.compose.models.about.AboutViewModelPreview
import com.blockstream.compose.navigation.NavigateDestinations
import com.blockstream.compose.theme.bodyMedium
import com.blockstream.compose.theme.titleMedium
import com.blockstream.compose.theme.titleSmall
import com.blockstream.compose.utils.SetupScreen
import com.blockstream.compose.utils.appTestTag
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * About: the app's own name and version, where the code came from, and Privacy, Terms and
 * Licences, then how to write to the developer. Nothing here is Blockstream's logo, help desk
 * or policy; rows are set apart by space, with no rules between them.
 */
@Composable
fun AboutScreen(
    viewModel: AboutViewModelAbstract,
) {
    val subject = stringResource(Res.string.zeni_contact_subject)

    SetupScreen(viewModel = viewModel) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.zeni_app_name), style = titleMedium)
            Text(
                "${stringResource(Res.string.id_version)} ${viewModel.version}",
                style = bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                stringResource(Res.string.zeni_based_on),
                style = bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                stringResource(Res.string.zeni_not_affiliated),
                style = bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            AboutRow(stringResource(Res.string.zeni_privacy), "about_privacy") {
                viewModel.postEvent(NavigateDestinations.Privacy)
            }
            AboutRow(stringResource(Res.string.zeni_terms), "about_terms") {
                viewModel.postEvent(NavigateDestinations.Terms)
            }
            AboutRow(stringResource(Res.string.zeni_licences), "about_licences") {
                viewModel.postEvent(NavigateDestinations.Licences)
            }
            AboutRow(
                stringResource(Res.string.zeni_contact),
                "about_contact",
                detail = stringResource(Res.string.zeni_contact_detail)
            ) {
                viewModel.postEvent(AboutViewModel.LocalEvents.ClickContact(subject))
            }
        }
    }
}

@Composable
private fun AboutRow(title: String, tag: String, detail: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .appTestTag(tag)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = titleSmall)
        if (detail != null) Text(detail, style = bodyMedium)
    }
}

@Composable
@Preview
fun AboutScreenPreview() {
    GreenPreview {
        AboutScreen(viewModel = AboutViewModelPreview.preview())
    }
}
