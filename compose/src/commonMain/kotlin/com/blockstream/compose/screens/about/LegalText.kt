package com.blockstream.compose.screens.about

const val SOURCE_URL = "https://github.com/wanderwildwood/zeni"
const val UPSTREAM_URL = "https://github.com/Blockstream/green_android"
const val CONTACT_EMAIL = "email@wanderthe.dev"

/** An address that opens the person's mail program with a blank message to the developer. */
fun contactUri(subject: String = "Bitcoin (zeni)"): String =
    "mailto:$CONTACT_EMAIL?subject=" + subject.replace(" ", "%20").replace("(", "%28").replace(")", "%29")

/**
 * The full licence texts the Licenses page falls back on, by family: a library with a text of its
 * own (config/licenses, with its copyright line) shows that instead. The build-time list of
 * libraries comes from the AboutLibraries plugin in androidApp, whose check reads this folder.
 */
val licenceFamilyFiles: List<Pair<String, String>> = listOf(
    "gpl" to "files/gpl_3_0.txt",
    "apache" to "files/apache_2_0.txt",
    "ofl" to "files/ofl_1_1.txt",
)

/** TITLE is the About's app name; ACTION a row that does something in the app (its [LegalRow.url] is a key, not an address). */
enum class RowKind { HEADING, BODY, LINK, COMPONENT, TITLE, ACTION }

/** One screenful-friendly row of a legal page; the pager never splits a row. */
data class LegalRow(val text: String, val kind: RowKind = RowKind.BODY, val detail: String? = null, val url: String? = null)

private const val MAX_ROW_CHARS = 700

private fun stripMarkdown(line: String): String = line
    .replace(Regex("\\[([^]]+)]\\(([^)]+)\\)"), "$1")
    .replace(Regex("<(https?://[^>]+)>"), "$1")
    .replace("**", "")
    .replace("`", "")

/** Splits a long paragraph into rows of at most [MAX_ROW_CHARS], breaking after a full stop or a space. */
fun chunk(text: String, maxChars: Int = MAX_ROW_CHARS): List<String> {
    if (text.length <= maxChars) return listOf(text)
    val out = mutableListOf<String>()
    var rest = text
    while (rest.length > maxChars) {
        val window = rest.substring(0, maxChars)
        var cut = window.lastIndexOf(". ")
        if (cut < maxChars / 3) cut = window.lastIndexOf(' ') else cut += 1
        if (cut <= 0) cut = maxChars
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
fun legalRows(raw: String, markdown: Boolean = false, maxChars: Int = MAX_ROW_CHARS): List<LegalRow> {
    val rows = mutableListOf<LegalRow>()
    // HTML comments (the shared-about markers around the privacy section) are not shown.
    val paragraphs = raw.replace("\r", "").replace(Regex("<!--[\\s\\S]*?-->"), "")
        .split(Regex("\n\\s*\n"))
    for (paragraph in paragraphs) {
        val lines = paragraph.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) continue
        if (markdown && lines.first().startsWith("#")) {
            rows += LegalRow(stripMarkdown(lines.first().trimStart('#').trim()), RowKind.HEADING)
            val rest = lines.drop(1)
            if (rest.isNotEmpty()) rows += chunk(stripMarkdown(rest.joinToString(" ") { it.trim() }), maxChars).map { LegalRow(it) }
            continue
        }
        if (markdown && lines.first().trimStart().startsWith("- ")) {
            var current = StringBuilder()
            fun flush() {
                if (current.isNotEmpty()) rows += chunk("• " + stripMarkdown(current.toString().trim()), maxChars).map { LegalRow(it) }
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
        rows += chunk(if (markdown) stripMarkdown(joined) else joined, maxChars).map { LegalRow(it) }
    }
    return rows
}
