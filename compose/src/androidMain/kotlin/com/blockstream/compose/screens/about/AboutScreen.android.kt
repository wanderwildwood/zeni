package com.blockstream.compose.screens.about

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.font.FontWeight
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.about_close_drawer
import blockstream_green.common.generated.resources.about_contact_head
import blockstream_green.common.generated.resources.about_feed_the_llamas
import blockstream_green.common.generated.resources.about_links_head
import blockstream_green.common.generated.resources.about_no_mail_app
import blockstream_green.common.generated.resources.about_open_drawer
import blockstream_green.common.generated.resources.about_write_to
import blockstream_green.common.generated.resources.id_close
import blockstream_green.common.generated.resources.privacy_contact
import blockstream_green.common.generated.resources.privacy_contact_title
import blockstream_green.common.generated.resources.privacy_rights
import blockstream_green.common.generated.resources.privacy_rights_title
import blockstream_green.common.generated.resources.privacy_who
import blockstream_green.common.generated.resources.privacy_who_title
import blockstream_green.common.generated.resources.zeni_about_credits_title
import blockstream_green.common.generated.resources.zeni_about_what
import blockstream_green.common.generated.resources.zeni_app_name
import blockstream_green.common.generated.resources.zeni_based_on
import blockstream_green.common.generated.resources.zeni_licences
import blockstream_green.common.generated.resources.zeni_no_browser_address
import blockstream_green.common.generated.resources.zeni_not_affiliated
import blockstream_green.common.generated.resources.zeni_privacy
import blockstream_green.common.generated.resources.zeni_terms
import com.blockstream.compose.R
import com.blockstream.compose.events.Events
import com.blockstream.compose.models.about.AboutViewModelAbstract
import com.blockstream.compose.utils.SetupScreen
import org.jetbrains.compose.resources.stringResource

/**
 * This app's About: only the words and the addresses. The dialog, its drawers, its one Close
 * button, the Licenses drawer, the links and the llama are the shared HouseAbout.kt, the same in
 * every app. It opens over the About destination; Close or Back leaves both.
 *
 * Privacy and Terms are the whole of files/privacy.md and files/terms.md (PRIVACY.md and
 * TERMS.md), a row per paragraph; the policy's own "Who we are / How to contact us / Your rights"
 * block is left out of the Privacy drawer, since those are drawers of their own.
 */
@Composable
actual fun AboutScreen(viewModel: AboutViewModelAbstract) {
    val privacy by produceState(emptyList<String>()) { value = aboutDocument(readText("files/privacy.md")) }
    val terms by produceState(emptyList<String>()) { value = aboutDocument(readText("files/terms.md")) }

    val title = "${stringResource(Res.string.zeni_app_name)} ${viewModel.version}"
    val privacyTitle = stringResource(Res.string.zeni_privacy)
    val termsTitle = stringResource(Res.string.zeni_terms)
    val data = AboutData(
        title = title,
        sections = buildList {
            add(AboutSection(null, stringResource(Res.string.zeni_about_what)))
            add(AboutSection(privacyTitle, privacy.firstOrNull() ?: ""))
            privacy.drop(1).forEach { add(AboutSection(null, it)) }
            add(AboutSection(stringResource(Res.string.privacy_who_title), stringResource(Res.string.privacy_who)))
            add(AboutSection(stringResource(Res.string.privacy_contact_title), stringResource(Res.string.privacy_contact)))
            add(AboutSection(stringResource(Res.string.privacy_rights_title), stringResource(Res.string.privacy_rights)))
            add(AboutSection(termsTitle, terms.firstOrNull() ?: ""))
            terms.drop(1).forEach { add(AboutSection(null, it)) }
            add(AboutSection(stringResource(Res.string.zeni_about_credits_title), stringResource(Res.string.zeni_based_on)))
            add(AboutSection(null, stringResource(Res.string.zeni_not_affiliated)))
        },
        sourceUrl = SOURCE_URL,
        links = listOf(AboutLinkData(aboutAddress(UPSTREAM_URL), UPSTREAM_URL)),
    )
    // Filled in later with an address or a drawer's heading.
    val noBrowser = stringResource(Res.string.zeni_no_browser_address, "%1\$s")
    val writeTo = stringResource(Res.string.about_write_to, "%1\$s")
    val noMailApp = stringResource(Res.string.about_no_mail_app, "%1\$s")
    val openDrawer = stringResource(Res.string.about_open_drawer, "%1\$s")
    val closeDrawer = stringResource(Res.string.about_close_drawer, "%1\$s")
    val labels = AboutLabels(
        close = stringResource(Res.string.id_close),
        licences = stringResource(Res.string.zeni_licences),
        feedTheLlamas = stringResource(Res.string.about_feed_the_llamas),
        noBrowser = { noBrowser.replace("%1\$s", it) },
        writeTo = { writeTo.replace("%1\$s", it) },
        noMailApp = { noMailApp.replace("%1\$s", it) },
        openDrawer = { openDrawer.replace("%1\$s", it) },
        closeDrawer = { closeDrawer.replace("%1\$s", it) },
        contact = stringResource(Res.string.about_contact_head),
        links = stringResource(Res.string.about_links_head),
    )
    val close = { viewModel.postEvent(Events.NavigateBackUserAction) }

    SetupScreen(viewModel = viewModel, withPadding = false) {
        // zeni's labelSmall is Green's bold label (16 sp); the About reads it as body text, so the
        // drawers' bold headings stand out from their regular text, as in every other app.
        val type = MaterialTheme.typography
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            shapes = MaterialTheme.shapes,
            typography = type.copy(labelSmall = type.labelSmall.copy(fontWeight = FontWeight.Normal)),
        ) {
            if (privacy.isNotEmpty() && terms.isNotEmpty()) {
                HouseAboutDialog(data, labels, R.drawable.about_llama, onDismiss = close)
            }
        }
    }
}

private suspend fun readText(path: String): String = Res.readBytes(path).decodeToString()

/** The policy's own copy of the shared privacy section, between its markers. */
private val SHARED_PRIVACY = Regex("<!-- shared-about privacy section -->[\\s\\S]*?<!-- /shared-about privacy section -->")

/**
 * A Markdown document (privacy.md, terms.md) as the paragraphs of one drawer: its title and the
 * shared privacy block left out, its sub-headings kept as lines of their own, paragraphs cut to a
 * length that fits on a page.
 *
 * The documents name the contact address too, and HouseAbout makes the first paragraph that gives
 * it the contact row. That row belongs to "How to contact us", so here the address carries an
 * invisible word joiner (U+2060) after the @: it reads and wraps the same, but is not matched.
 */
fun aboutDocument(markdown: String): List<String> =
    legalRows(markdown.replace(SHARED_PRIVACY, ""), markdown = true, maxChars = ABOUT_ROW_CHARS)
        .drop(1)
        .map { it.text.replace(ABOUT_CONTACT_EMAIL, ABOUT_CONTACT_EMAIL.replace("@", "@\u2060")) }

private const val ABOUT_ROW_CHARS = 420
