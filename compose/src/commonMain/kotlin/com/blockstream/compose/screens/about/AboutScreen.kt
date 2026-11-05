package com.blockstream.compose.screens.about

import androidx.compose.runtime.Composable
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_version
import blockstream_green.common.generated.resources.zeni_app_name
import blockstream_green.common.generated.resources.zeni_based_on
import blockstream_green.common.generated.resources.about_write_to
import blockstream_green.common.generated.resources.privacy_contact
import blockstream_green.common.generated.resources.privacy_contact_title
import blockstream_green.common.generated.resources.privacy_rights
import blockstream_green.common.generated.resources.privacy_rights_title
import blockstream_green.common.generated.resources.privacy_who
import blockstream_green.common.generated.resources.privacy_who_title
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
import com.blockstream.compose.utils.SetupScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * About. On Android it is the shared drawer About (HouseAbout, AboutScreen.android.kt): a dialog
 * over this destination, closed by Close or Back. Desktop and iOS, which are not shipped, keep
 * [AboutRowsScreen].
 */
@Composable
expect fun AboutScreen(viewModel: AboutViewModelAbstract)

/**
 * The About as plain rows, for desktop and iOS: the app's own name and version, where the code came
 * from, Privacy with its three short sections (who we are, how to contact us, your rights), Terms
 * and Licenses, then "Write to email@wanderthe.dev". Rows turn on whole pages.
 */
@Composable
fun AboutRowsScreen(
    viewModel: AboutViewModelAbstract,
) {
    val subject = stringResource(Res.string.zeni_contact_subject)

    val rows = listOf(
        LegalRow(stringResource(Res.string.zeni_app_name), RowKind.TITLE),
        LegalRow("${stringResource(Res.string.id_version)} ${viewModel.version}"),
        LegalRow(stringResource(Res.string.zeni_based_on)),
        LegalRow(stringResource(Res.string.zeni_not_affiliated)),
        LegalRow(stringResource(Res.string.zeni_privacy), RowKind.ACTION, url = ABOUT_PRIVACY),
        LegalRow(stringResource(Res.string.privacy_who_title), RowKind.HEADING),
        LegalRow(stringResource(Res.string.privacy_who)),
        LegalRow(stringResource(Res.string.privacy_contact_title), RowKind.HEADING),
        LegalRow(stringResource(Res.string.privacy_contact)),
        LegalRow(stringResource(Res.string.privacy_rights_title), RowKind.HEADING),
        LegalRow(stringResource(Res.string.privacy_rights)),
        LegalRow(stringResource(Res.string.zeni_terms), RowKind.ACTION, url = ABOUT_TERMS),
        LegalRow(stringResource(Res.string.zeni_licences), RowKind.ACTION, url = ABOUT_LICENCES),
        // The one contact address, written out in full; pressing it opens the email app.
        LegalRow(stringResource(Res.string.about_write_to, CONTACT_EMAIL), RowKind.ACTION, url = ABOUT_CONTACT),
    )

    // Turned a page at a time on whole rows, measured as drawn, like Privacy and Terms.
    SetupScreen(viewModel = viewModel, withPadding = false) {
        PagedRows(rows, onOpen = { key ->
            when (key) {
                ABOUT_PRIVACY -> viewModel.postEvent(NavigateDestinations.Privacy)
                ABOUT_TERMS -> viewModel.postEvent(NavigateDestinations.Terms)
                ABOUT_LICENCES -> viewModel.postEvent(NavigateDestinations.Licences)
                ABOUT_CONTACT -> viewModel.postEvent(AboutViewModel.LocalEvents.ClickContact(subject))
            }
        })
    }
}

private const val ABOUT_PRIVACY = "about:privacy"
private const val ABOUT_TERMS = "about:terms"
private const val ABOUT_LICENCES = "about:licences"
private const val ABOUT_CONTACT = "about:contact"

@Composable
@Preview
fun AboutScreenPreview() {
    GreenPreview {
        AboutRowsScreen(viewModel = AboutViewModelPreview.preview())
    }
}
