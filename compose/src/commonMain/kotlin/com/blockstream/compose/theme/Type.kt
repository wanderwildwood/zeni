package com.blockstream.compose.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.lato_bold
import blockstream_green.common.generated.resources.lato_regular
import blockstream_green.common.generated.resources.lato_semi_bold
import blockstream_green.common.generated.resources.monospace_bold
import blockstream_green.common.generated.resources.monospace_regular
import org.jetbrains.compose.resources.Font

@Composable
// MMD's Lato in place of Inter. Thin and extra-light map to regular: the e-ink panel loses
// the thin end of every stroke, so nothing on this screen is set lighter than regular.
fun InterFont() = FontFamily(
    Font(Res.font.lato_regular, FontWeight.Normal),
    Font(Res.font.lato_regular, FontWeight.Thin),
    Font(Res.font.lato_regular, FontWeight.ExtraLight),
    Font(Res.font.lato_regular, FontWeight.Light),
    Font(Res.font.lato_semi_bold, FontWeight.Medium),
    Font(Res.font.lato_semi_bold, FontWeight.SemiBold),
    Font(Res.font.lato_bold, FontWeight.Bold),
)

@Composable
@Stable
fun MonospaceFont() = FontFamily(
    Font(Res.font.monospace_regular, FontWeight.Normal),
    Font(Res.font.monospace_bold, FontWeight.Bold),
)

@Composable
fun copyTextStyle(
    textStyle: TextStyle,
    fontSize: TextUnit = textStyle.fontSize,
    fontWeight: FontWeight? = textStyle.fontWeight,
    lineHeight: TextUnit = textStyle.lineHeight
): TextStyle = textStyle.copy(
    fontFamily = InterFont(),
    fontSize = fontSize,
    fontWeight = fontWeight,
    lineHeight = lineHeight
)

/*
 * Nothing smaller than 16 sp (Mudita's review, 4 Nov 2026, and STYLE.md; the same floor as
 * shared-housetype's houseTypography, which the MMD-themed apps use). Helper text measured 22 px
 * on the Kompakt, and Mudita asks for at least 24 px. The Kompakt draws at 213 dpi, so a 14 sp
 * line of Lato is 22.4 px tall and 16 sp is 25.6 px. zeni keeps Green's own scale, so its six
 * small steps (bodyLarge, bodyMedium, bodySmall and the three labels, 14 and 15 sp) are raised
 * to the floor here, with line heights of at least 1.2 times the size so nothing clips.
 */

/** The smallest size the type scale sets, in sp. Type below it needs a written reason (STYLE.md). */
const val HOUSE_TYPE_FLOOR_SP: Float = 16f

/** The floor as a TextUnit, for the rare call that must set a size of its own. */
val HouseTypeFloor: TextUnit = HOUSE_TYPE_FLOOR_SP.sp

/** [base] with every style smaller than the floor set to the floor; the rest untouched. */
fun raiseToFloor(base: Typography): Typography {
    fun up(s: TextStyle) =
        if (s.fontSize.isSp && s.fontSize.value < HOUSE_TYPE_FLOOR_SP) s.copy(fontSize = HouseTypeFloor) else s
    return base.copy(
        displayLarge = up(base.displayLarge), displayMedium = up(base.displayMedium),
        displaySmall = up(base.displaySmall), headlineLarge = up(base.headlineLarge),
        headlineMedium = up(base.headlineMedium), headlineSmall = up(base.headlineSmall),
        titleLarge = up(base.titleLarge), titleMedium = up(base.titleMedium),
        titleSmall = up(base.titleSmall), bodyLarge = up(base.bodyLarge),
        bodyMedium = up(base.bodyMedium), bodySmall = up(base.bodySmall),
        labelLarge = up(base.labelLarge), labelMedium = up(base.labelMedium),
        labelSmall = up(base.labelSmall),
    )
}

@Composable
fun GreenTypography() = Typography().let {
    raiseToFloor(Typography(
        displayLarge = copyTextStyle(it.displayLarge, fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 42.sp),
        displayMedium = copyTextStyle(it.displayMedium, fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
        displaySmall = copyTextStyle(it.displaySmall, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 38.sp),

        headlineLarge = copyTextStyle(it.headlineLarge, fontSize = 26.sp, fontWeight = FontWeight.Bold),
        headlineMedium = copyTextStyle(it.headlineMedium, fontSize = 24.sp, fontWeight = FontWeight.Bold),
        headlineSmall = copyTextStyle(it.headlineSmall, fontSize = 22.sp, fontWeight = FontWeight.Bold),

        titleLarge = copyTextStyle(it.titleLarge, fontSize = 20.sp, fontWeight = FontWeight.Bold),
        titleMedium = copyTextStyle(it.titleMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold),
        titleSmall = copyTextStyle(it.titleSmall, fontSize = 16.sp, fontWeight = FontWeight.Bold),

        bodyLarge = copyTextStyle(it.bodyLarge, fontSize = HouseTypeFloor, lineHeight = 24.sp),
        bodyMedium = copyTextStyle(it.bodyMedium, fontSize = HouseTypeFloor, lineHeight = 20.sp),
        bodySmall = copyTextStyle(it.bodySmall, fontSize = HouseTypeFloor, lineHeight = 20.sp),

        labelLarge = copyTextStyle(it.labelLarge, fontSize = HouseTypeFloor, lineHeight = 24.sp, fontWeight = FontWeight.Bold),
        labelMedium = copyTextStyle(it.labelMedium, fontSize = HouseTypeFloor, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
        labelSmall = copyTextStyle(it.labelSmall, fontSize = HouseTypeFloor, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
    ))
}

val displayLarge
    @Composable
    get() = MaterialTheme.typography.displayLarge

val displayMedium
    @Composable
    get() = MaterialTheme.typography.displayMedium

val displaySmall
    @Composable
    get() = MaterialTheme.typography.displaySmall

val headlineLarge
    @Composable
    get() = MaterialTheme.typography.headlineLarge

val headlineMedium
    @Composable
    get() = MaterialTheme.typography.headlineMedium

val headlineSmall
    @Composable
    get() = MaterialTheme.typography.headlineSmall

val titleLarge
    @Composable
    get() = MaterialTheme.typography.titleLarge

val titleMedium
    @Composable
    get() = MaterialTheme.typography.titleMedium

val titleSmall
    @Composable
    get() = MaterialTheme.typography.titleSmall

val bodyLarge
    @Composable
    get() = MaterialTheme.typography.bodyLarge

val bodyMedium
    @Composable
    get() = MaterialTheme.typography.bodyMedium

val bodySmall
    @Composable
    get() = MaterialTheme.typography.bodySmall

val labelLarge
    @Composable
    get() = MaterialTheme.typography.labelLarge

val labelMedium
    @Composable
    get() = MaterialTheme.typography.labelMedium

val labelSmall
    @Composable
    get() = MaterialTheme.typography.labelSmall

// Text Colors
val textHigh
    @Composable
    get() = LocalContentColor.current

val textMedium
    @Composable
    get() = LocalContentColor.current

val textLow
    @Composable
    get() = LocalContentColor.current