package com.blockstream.compose.screens.about

const val SOURCE_URL = "https://github.com/wanderwildwood/zeni"
const val UPSTREAM_URL = "https://github.com/Blockstream/green_android"
const val CONTACT_EMAIL = "email@wanderthe.dev"

/** An address that opens the person's mail program with a blank message to the developer. */
fun contactUri(subject: String = "Bitcoin (zeni)"): String =
    "mailto:$CONTACT_EMAIL?subject=" + subject.replace(" ", "%20").replace("(", "%28").replace(")", "%29")

/** One thing the app is built with: its name, its licence and where it lives. */
data class Component(val name: String, val licence: String, val link: String)

/**
 * Everything the APK contains that is not this project's own code, read from the built APK
 * (its dex packages, native libraries and assets). Libraries come in families: one line each.
 */
val components: List<Component> = listOf(
    Component("Blockstream Green 5.7.0, the wallet this app is a fork of", "GPL-3.0", UPSTREAM_URL),
    Component("Blockstream GDK, the wallet library (native)", "MIT", "https://github.com/Blockstream/gdk"),
    Component("libwally-core and libsecp256k1, inside GDK", "MIT", "https://github.com/ElementsProject/libwally-core"),
    Component("Tor, inside GDK", "BSD-3-Clause", "https://www.torproject.org"),
    Component("Blockstream Greenlight SDK (glsdk), present but unused", "MIT", "https://github.com/Blockstream/greenlight"),
    Component("Liquid Wallet Kit (lwk), present but unused", "MIT", "https://github.com/Blockstream/lwk"),
    Component("BTChip Java API, Ledger support (source in this repository)", "Apache-2.0", "https://github.com/LedgerHQ"),
    Component("Countly SDK, present but switched off", "MIT", "https://github.com/countly/countly-sdk-android"),
    Component("Rive Android", "MIT", "https://github.com/rive-app/rive-android"),
    Component("JNA", "Apache-2.0", "https://github.com/java-native-access/jna"),
    Component("Kotlin standard library, coroutines, serialization, datetime, io, atomicfu", "Apache-2.0", "https://kotlinlang.org"),
    Component("Jetpack Compose, Compose Multiplatform, AndroidX (Core, Lifecycle, Navigation, Room, Work, Biometric, Browser, AppCompat and others)", "Apache-2.0", "https://developer.android.com/jetpack"),
    Component("ConstraintLayout for Compose Multiplatform", "Apache-2.0", "https://developer.android.com/jetpack/androidx/releases/constraintlayout"),
    Component("Ktor", "Apache-2.0", "https://ktor.io"),
    Component("OkHttp and Okio", "Apache-2.0", "https://square.github.io/okhttp"),
    Component("Volley", "Apache-2.0", "https://github.com/google/volley"),
    Component("Kable, Bluetooth", "Apache-2.0", "https://github.com/JuulLabs/kable"),
    Component("Koin", "Apache-2.0", "https://insert-koin.io"),
    Component("SQLDelight", "Apache-2.0", "https://github.com/sqldelight/sqldelight"),
    Component("Kermit, Stately, Tuulbox", "Apache-2.0", "https://github.com/touchlab/Kermit"),
    Component("Multiplatform Settings, uri-kmp, Essenty state-keeper, Ksoup entities", "Apache-2.0", "https://github.com/russhwolf/multiplatform-settings"),
    Component("Coil", "Apache-2.0", "https://coil-kt.github.io/coil"),
    Component("Jackson", "Apache-2.0", "https://github.com/FasterXML/jackson"),
    Component("Guava (Google common)", "Apache-2.0", "https://github.com/google/guava"),
    Component("Accompanist permissions", "Apache-2.0", "https://github.com/google/accompanist"),
    Component("ZXing and ZXing Android Embedded, QR codes", "Apache-2.0", "https://github.com/zxing/zxing"),
    Component("ReLinker", "Apache-2.0", "https://github.com/KeepSafe/ReLinker"),
    Component("FileKit and mpfilepicker, file dialogs", "Apache-2.0", "https://github.com/vinceglb/FileKit"),
    Component("KotlinCrypto hash", "Apache-2.0", "https://github.com/KotlinCrypto/hash"),
    Component("Protocol Buffers", "BSD-3-Clause", "https://github.com/protocolbuffers/protobuf"),
    Component("kotlin-retry", "ISC", "https://github.com/michaelbull/kotlin-retry"),
    Component("QRose, QR drawing", "MIT", "https://github.com/alexzhirkevich/qrose"),
    Component("Koalaplot, charts", "MIT", "https://github.com/koalaplot/koalaplot-core"),
    Component("Phosphor Icons for Compose", "MIT", "https://github.com/phosphor-icons/core"),
    Component("UsbSerial, USB hardware wallets", "MIT", "https://github.com/felHR85/UsbSerial"),
    Component("SLF4J API", "MIT", "https://www.slf4j.org"),
    Component("Lato typeface", "OFL-1.1", "https://www.latofonts.com"),
)

/** A licence text bundled with the app: the name shown, the resource file, and its licence label. */
data class LicenceText(val name: String, val file: String)

val licenceTexts: List<LicenceText> = listOf(
    LicenceText("Apache-2.0", "files/apache_2_0.txt"),
    LicenceText("MIT", "files/mit.txt"),
    LicenceText("BSD-3-Clause", "files/bsd_3_clause.txt"),
    LicenceText("ISC", "files/isc.txt"),
    LicenceText("OFL-1.1 (Lato)", "files/ofl_1_1.txt"),
)

/** Every licence label a component uses must have its text bundled; GPL-3.0 has its own section. */
fun missingLicenceTexts(): List<String> {
    val shown = licenceTexts.map { it.name.substringBefore(' ') }.toSet() + "GPL-3.0"
    return components.map { it.licence }.distinct().filter { it !in shown }
}

enum class RowKind { HEADING, BODY, LINK, COMPONENT }

/** One screenful-friendly row of a legal page; the pager never splits a row. */
data class LegalRow(val text: String, val kind: RowKind = RowKind.BODY, val detail: String? = null, val url: String? = null)

private const val MAX_ROW_CHARS = 700

private fun stripMarkdown(line: String): String = line
    .replace(Regex("\\[([^]]+)]\\(([^)]+)\\)"), "$1")
    .replace(Regex("<(https?://[^>]+)>"), "$1")
    .replace("**", "")
    .replace("`", "")

/** Splits a long paragraph into rows of at most [MAX_ROW_CHARS], breaking after a full stop or a space. */
fun chunk(text: String): List<String> {
    if (text.length <= MAX_ROW_CHARS) return listOf(text)
    val out = mutableListOf<String>()
    var rest = text
    while (rest.length > MAX_ROW_CHARS) {
        val window = rest.substring(0, MAX_ROW_CHARS)
        var cut = window.lastIndexOf(". ")
        if (cut < MAX_ROW_CHARS / 3) cut = window.lastIndexOf(' ') else cut += 1
        if (cut <= 0) cut = MAX_ROW_CHARS
        out += rest.substring(0, cut).trim()
        rest = rest.substring(cut).trim()
    }
    if (rest.isNotEmpty()) out += rest
    return out
}

/**
 * Turns hard-wrapped plain text, or the small Markdown subset used by PRIVACY.md and TERMS.md,
 * into rows: blank lines break paragraphs, single line breaks become spaces, "#" lines are
 * headings and "- " lines are bullets of their own.
 */
fun legalRows(raw: String, markdown: Boolean = false): List<LegalRow> {
    val rows = mutableListOf<LegalRow>()
    val paragraphs = raw.replace("\r", "").split(Regex("\n\\s*\n"))
    for (paragraph in paragraphs) {
        val lines = paragraph.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) continue
        if (markdown && lines.first().startsWith("#")) {
            rows += LegalRow(stripMarkdown(lines.first().trimStart('#').trim()), RowKind.HEADING)
            val rest = lines.drop(1)
            if (rest.isNotEmpty()) rows += chunk(stripMarkdown(rest.joinToString(" ") { it.trim() })).map { LegalRow(it) }
            continue
        }
        if (markdown && lines.first().trimStart().startsWith("- ")) {
            var current = StringBuilder()
            fun flush() {
                if (current.isNotEmpty()) rows += chunk("• " + stripMarkdown(current.toString().trim())).map { LegalRow(it) }
                current = StringBuilder()
            }
            for (l in lines) {
                if (l.trimStart().startsWith("- ")) { flush(); current.append(l.trimStart().removePrefix("- ")) }
                else current.append(' ').append(l.trim())
            }
            flush()
            continue
        }
        val joined = lines.joinToString(" ") { it.trim() }
        rows += chunk(if (markdown) stripMarkdown(joined) else joined).map { LegalRow(it) }
    }
    return rows
}
