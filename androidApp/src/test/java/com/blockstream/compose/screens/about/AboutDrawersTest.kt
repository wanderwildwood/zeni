package com.blockstream.compose.screens.about

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** zeni's drawer About: Privacy and Terms are the bundled documents, the contact row stays in "How to contact us". */
class AboutDrawersTest {
    private val files = File(File("..").canonicalFile, "compose/src/commonMain/composeResources/files")
    private val privacy = aboutDocument(File(files, "privacy.md").readText())
    private val terms = aboutDocument(File(files, "terms.md").readText())

    private fun data() = AboutData(
        title = "Bitcoin 0.1.6",
        sections = buildList {
            add(AboutSection(null, "A Bitcoin wallet for a Blockstream Jade."))
            add(AboutSection("Privacy", privacy.first()))
            privacy.drop(1).forEach { add(AboutSection(null, it)) }
            add(AboutSection("Who we are", "Made by wander wildwood."))
            add(AboutSection("How to contact us", "Write to email@wanderthe.dev, about your data or anything else."))
            add(AboutSection("Your rights", "Under the GDPR you can ask."))
            add(AboutSection("Terms", terms.first()))
            terms.drop(1).forEach { add(AboutSection(null, it)) }
            add(AboutSection("Credits", "Based on Blockstream Green."))
        },
        sourceUrl = SOURCE_URL,
        links = listOf(AboutLinkData(aboutAddress(UPSTREAM_URL), UPSTREAM_URL)),
    )

    @Test
    fun documentsLoseTheirTitleAndTheSharedBlock() {
        assertFalse(privacy.first().contains("Privacy Policy"))
        assertFalse(terms.first().contains("Terms of Use"))
        assertTrue(privacy.none { it == "Who we are" || it == "Your rights" })
        assertTrue(privacy.any { it.startsWith("Blockstream's Electrum servers") || it.startsWith("• Blockstream's Electrum servers") })
        assertTrue(terms.any { it.contains("No warranty") })
    }

    @Test
    fun paragraphsAreShortEnoughForAPage() {
        (privacy + terms).forEach { assertTrue(it, it.length <= 420) }
    }

    @Test
    fun theContactRowIsTheHowToContactUsSentence() {
        val drawers = aboutDrawers(data())
        val holder = drawers.single { it.contact != null }
        assertEquals("How to contact us", holder.heading)
        val items = aboutItems(aboutRows(data()), drawers.map { it.key }.toSet(), "Licenses") { emptyList() }
        val contacts = items.filterIsInstance<AboutItem.Contact>()
        assertEquals(1, contacts.size)
        assertEquals("Write to email@wanderthe.dev, about your data or anything else.", contacts.single().label)
    }

    @Test
    fun theAboutHasNoProblems() {
        assertEquals(emptyList<String>(), aboutProblems(data()))
        assertEquals(listOf("A Bitcoin wallet for a Blockstream Jade."), aboutIntro(data()))
    }
}
