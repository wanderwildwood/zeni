package com.blockstream.compose.screens.about

import com.blockstream.data.data.EncryptedData
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegalTextTest {
    private val root = File("..").canonicalFile
    private val res = File(root, "compose/src/commonMain/composeResources")

    private fun read(path: String) = File(path).readText()

    @Test
    fun builtWithListIsNotEmptyAndEveryLicenceHasItsText() {
        assertTrue(components.size > 10)
        assertTrue(components.all { it.name.isNotBlank() && it.link.startsWith("https://") })
        assertEquals(emptyList<String>(), missingLicenceTexts())
        for (t in licenceTexts) assertTrue(t.file, File(res, t.file).readText().length > 500)
    }

    @Test
    fun gplTextIsBundledInFull() {
        val gpl = File(res, "files/gpl_3_0.txt").readText()
        assertTrue(gpl.contains("GNU GENERAL PUBLIC LICENSE"))
        assertTrue(gpl.contains("END OF TERMS AND CONDITIONS"))
        assertTrue(legalRows(gpl).size > 100)
    }

    @Test
    fun theSourceLinkAndContactBuild() {
        assertEquals("https://github.com/wanderwildwood/zeni", SOURCE_URL)
        assertTrue(contactUri("Bitcoin (zeni)").startsWith("mailto:email@wanderthe.dev?subject=Bitcoin%20%28zeni%29"))
    }

    @Test
    fun theBundledPrivacyAndTermsAreTheRepositoryFiles() {
        assertEquals(File(root, "PRIVACY.md").readText(), File(res, "files/privacy.md").readText())
        assertEquals(File(root, "TERMS.md").readText(), File(res, "files/terms.md").readText())
    }

    @Test
    fun privacyAndTermsSayWeAreNotBlockstream() {
        for (f in listOf("PRIVACY.md", "TERMS.md")) {
            val t = File(root, f).readText()
            assertTrue(f, t.contains("email@wanderthe.dev"))
            assertTrue(f, Regex("not (made, )?(endorsed|affiliated)", RegexOption.IGNORE_CASE).containsMatchIn(t) ||
                t.contains("not made, endorsed or supported by Blockstream"))
        }
    }

    @Test
    fun markdownBecomesPlainRows() {
        val rows = legalRows("# Title\n\nSome **bold** and `code`, see [the site](https://x.y).\n\n- one\n  more\n- two", markdown = true)
        assertEquals(RowKind.HEADING, rows[0].kind)
        assertEquals("Title", rows[0].text)
        assertEquals("Some bold and code, see the site.", rows[1].text)
        assertEquals("• one more", rows[2].text)
        assertEquals("• two", rows[3].text)
    }

    @Test
    fun longParagraphsAreCutIntoRowsThatFitAPage() {
        val long = (1..200).joinToString(" ") { "Sentence number $it." }
        val rows = chunk(long)
        assertTrue(rows.size > 3)
        assertTrue(rows.all { it.length <= 700 })
        assertEquals(long, rows.joinToString(" "))
    }

    @Test
    fun pagingMovesOnWholeRowsAndNeverSticks() {
        assertEquals(5, nextPageStart(firstVisible = 0, lastFullyVisible = 4, lastVisible = 5, count = 20))
        // a row taller than the viewport still advances by one
        assertEquals(4, nextPageStart(firstVisible = 3, lastFullyVisible = null, lastVisible = 3, count = 20))
        assertEquals(20, nextPageStart(firstVisible = 18, lastFullyVisible = 19, lastVisible = 19, count = 20))
    }

    @Test
    fun englishStringsDoNotCallThisAppBlockstream() {
        val xml = File(res, "values/strings.xml").readText()
        for (banned in listOf("Blockstream App", "Blockstream app", "Blockstream Green wallet", "Unlock Green")) {
            assertFalse(banned, xml.contains(banned))
        }
    }

    @Test
    fun manifestClaimsNoBlockstreamAddresses() {
        val manifest = File(root, "androidApp/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("blockstream.com"))
        assertFalse(manifest.contains("scheme=\"blockstream\""))
    }

    @Test
    fun noRemainingBlockstreamLogoOrSocialDrawables() {
        for (n in listOf("blockstream_logo", "brand", "x_logo", "facebook_logo", "telegram_logo", "youtube_logo", "linkedin_logo", "github_logo")) {
            assertFalse(n, File(res, "drawable/$n.xml").exists())
        }
    }

    @Test
    fun gcmDataIsToldFromOldCbcDataByItsNonceLength() {
        assertTrue(EncryptedData.fromByteArray(byteArrayOf(1), ByteArray(12)).isGcm())
        assertFalse(EncryptedData.fromByteArray(byteArrayOf(1), ByteArray(16)).isGcm())
    }

    @Test
    fun nothingInOurCodeAsksForCbcAnyMoreExceptTheLegacyReader() {
        val ks = File(root, "data/src/androidMain/kotlin/com/blockstream/data/utils/AndroidKeystore.kt").readText()
        // new keys are GCM, and the only CBC key is generated for the v1 alias read once
        assertTrue(ks.contains("BLOCK_MODE_GCM"))
        assertTrue(ks.contains("const val BASIC_GCM_ALIAS = \"v2-basic\""))
        assertTrue(ks.contains("const val BIOMETRICS_GCM_ALIAS = \"v2-biometrics\""))
        val encrypting = ks.substringAfter("override fun encryptData(dataToEncrypt: ByteArray)").substringBefore("fun encryptData(cipher: Cipher")
        assertTrue(encrypting.contains("BASIC_GCM_ALIAS"))
        assertFalse(encrypting.contains("BASIC_KEYSTORE_ALIAS"))
        val biometrics = ks.substringAfter("fun getBiometricsEncryptionCipher").substringBefore("In most cases")
        assertTrue(biometrics.contains("BIOMETRICS_GCM_ALIAS"))
        assertFalse(biometrics.contains("BIOMETRICS_KEYSTORE_ALIAS"))
    }
}
