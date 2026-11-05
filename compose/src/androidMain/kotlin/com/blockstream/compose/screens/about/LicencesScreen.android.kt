package com.blockstream.compose.screens.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_back
import blockstream_green.common.generated.resources.zeni_about_licence_line
import blockstream_green.common.generated.resources.zeni_app_name
import blockstream_green.common.generated.resources.zeni_licence_no_text
import blockstream_green.common.generated.resources.zeni_licences
import blockstream_green.common.generated.resources.zeni_no_browser_address
import blockstream_green.common.generated.resources.zeni_source_code
import com.blockstream.compose.events.Events
import com.blockstream.compose.models.about.LegalViewModel
import com.blockstream.compose.screens.about.licences.LicenceText
import com.blockstream.compose.screens.about.licences.LicencesData
import com.blockstream.compose.screens.about.licences.LicencesLabels
import com.blockstream.compose.screens.about.licences.generatedLicencesResId
import com.blockstream.compose.screens.about.licences.readGeneratedComponents
import com.blockstream.compose.utils.SetupScreen
import org.jetbrains.compose.resources.stringResource
import com.blockstream.compose.screens.about.licences.LicencesScreen as HouseLicencesScreen

/**
 * The libraries for the About's Licenses drawer (and the Licenses page): the app's own row, then
 * one row per library the build packed. The list is the AboutLibraries plugin's
 * res/raw/aboutlibraries.json from androidApp, found by name because this module cannot see that
 * module's R; the family texts are this module's Compose resources (the .txt files under files),
 * read once, so the family texts are empty for the first frame.
 */
@Composable
fun rememberLicencesData(): LicencesData {
    val context = LocalContext.current
    val components = remember { readGeneratedComponents(context, generatedLicencesResId(context)) }
    val texts by produceState(emptyList<LicenceText>()) {
        value = licenceFamilyFiles.map { (id, file) ->
            LicenceText(id, id, 0, Res.readBytes(file).decodeToString())
        }
    }
    val appName = stringResource(Res.string.zeni_app_name)
    val licenceLine = stringResource(Res.string.zeni_about_licence_line)
    return remember(components, texts, appName, licenceLine) {
        LicencesData(appName, SOURCE_URL, licenceLine, components, texts)
    }
}

/** The words of the Licenses list, from the app's strings. */
@Composable
fun rememberLicencesLabels(): LicencesLabels {
    // Filled in later with a license's name or an address.
    val noText = stringResource(Res.string.zeni_licence_no_text, "%1\$s")
    val noBrowser = stringResource(Res.string.zeni_no_browser_address, "%1\$s")
    return LicencesLabels(
        title = stringResource(Res.string.zeni_licences),
        back = stringResource(Res.string.id_back),
        sourceHeading = stringResource(Res.string.zeni_source_code),
        noText = { noText.replace("%1\$s", it) },
        noBrowser = { noBrowser.replace("%1\$s", it) },
    )
}

/**
 * The house Licenses screen (shared-licences) as a page of its own. The About no longer opens it
 * (Licenses is a drawer there); the route stays for the desktop and iOS About.
 */
@Composable
actual fun LicencesScreen(viewModel: LegalViewModel) {
    val data = rememberLicencesData()
    val labels = rememberLicencesLabels()
    SetupScreen(viewModel = viewModel, withPadding = false) {
        HouseLicencesScreen(
            data = data,
            labels = labels,
            onBack = { viewModel.postEvent(Events.NavigateBackUserAction) },
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            showBackButton = false,
            showTitle = false,
        )
    }
}
