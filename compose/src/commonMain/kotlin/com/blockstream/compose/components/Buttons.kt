package com.blockstream.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.arrow_square_out
import blockstream_green.common.generated.resources.info
import blockstream_green.common.generated.resources.clipboard
import blockstream_green.common.generated.resources.id_about
import blockstream_green.common.generated.resources.id_app_settings
import blockstream_green.common.generated.resources.id_biometrics
import blockstream_green.common.generated.resources.id_help
import blockstream_green.common.generated.resources.id_increase_qr_size
import blockstream_green.common.generated.resources.id_learn_more
import blockstream_green.common.generated.resources.id_paste
import blockstream_green.common.generated.resources.id_scan_qr_code
import blockstream_green.common.generated.resources.magnifying_glass_plus
import blockstream_green.common.generated.resources.qr_code
import blockstream_green.common.generated.resources.question
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowCounterClockwise
import com.adamglin.phosphoricons.regular.Spinner
import com.blockstream.compose.theme.green
import com.blockstream.compose.theme.labelLarge
import com.blockstream.compose.theme.labelMedium
import com.blockstream.compose.theme.labelSmall
import com.blockstream.compose.theme.textHigh
import com.blockstream.compose.theme.textMedium
import com.blockstream.compose.theme.whiteMedium
import com.blockstream.compose.utils.Rotating
import com.blockstream.compose.utils.appTestTag
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

enum class GreenButtonSize {
    NORMAL, SMALL, TINY, BIG, LARGE
}

enum class GreenButtonType {
    COLOR, OUTLINE, TEXT
}

enum class GreenButtonColor {
    GREEN, GREENER, WHITE, RED
}

@Composable
private fun GreenButtonText(text: String, textStyle: TextStyle) {
    Text(text, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
fun GreenButton(
    text: String,
    modifier: Modifier = Modifier,
    type: GreenButtonType = GreenButtonType.COLOR,
    color: GreenButtonColor = GreenButtonColor.GREEN,
    size: GreenButtonSize = GreenButtonSize.NORMAL,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null,
    onProgress: Boolean = false,
    onClick: () -> Unit
) {

    // MMD look: black and white, a 2 dp rim, 8 dp corners, never shorter than the 48 dp touch rule.
    // Colour variants (red, green) mean nothing on an E Ink panel; a destructive action asks with
    // the armed-row pattern instead. COLOR is the filled primary press, the rest are outlined.
    val sizedModifier = Modifier
        .then(modifier)
        .heightIn(min = 48.dp)
        .widthIn(min = 48.dp)
        .semantics { contentDescription = "$text Button" }
        .appTestTag(testTag)

    val contentPadding = PaddingValues(
        horizontal = if (size == GreenButtonSize.TINY || size == GreenButtonSize.SMALL) 8.dp else 14.dp,
        vertical = 8.dp
    )

    MmdButton(
        onClick = onClick,
        modifier = sizedModifier,
        enabled = enabled,
        filled = type == GreenButtonType.COLOR,
        contentPadding = contentPadding,
    ) {
        if (icon != null || onProgress) {
            Icon(
                imageVector = if (onProgress) PhosphorIcons.Regular.Spinner else icon ?: PhosphorIcons.Regular.ArrowCounterClockwise,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(18.dp)
            )
        }
        GreenButtonText(text = text, textStyle = labelLarge)
    }
}

@Composable
fun GreenIconButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    color: Color = green,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    testTag: String? = null,
    onClick: () -> Unit
) {
    MmdButton(
        onClick = onClick,
        modifier = Modifier.then(modifier).heightIn(min = 48.dp).widthIn(min = 48.dp),
        enabled = true,
        filled = false,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    ) {
        GreenRow(padding = 0, space = 6) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
                    .appTestTag(testTag)
            )

            Text(text = text, style = labelMedium)
        }
    }
}

@Composable
fun HelpButton(onClick: () -> Unit) {
    GreenIconButton(
        text = stringResource(Res.string.id_help),
        icon = painterResource(Res.drawable.question),
        color = whiteMedium,
        onClick = onClick
    )
}

@Composable
fun PasteButton(onClick: () -> Unit) {
    GreenIconButton(
        text = stringResource(Res.string.id_paste),
        icon = painterResource(Res.drawable.clipboard),
        testTag = "paste",
        onClick = onClick
    )
}

@Composable
fun ScanQrButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    GreenIconButton(
        modifier = modifier,
        text = stringResource(Res.string.id_scan_qr_code),
        icon = painterResource(Res.drawable.qr_code),
        testTag = "scan_qr",
        onClick = onClick
    )
}

@Composable
fun ZoomButton(onClick: () -> Unit) {
    GreenIconButton(
        text = stringResource(Res.string.id_increase_qr_size),
        icon = painterResource(Res.drawable.magnifying_glass_plus),
        color = textMedium,
        testTag = "increase_qr_size",
        onClick = onClick
    )
}

@Composable
fun ZoomOutlinedButton(onClick: () -> Unit) {
    MmdButton(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp),
        enabled = true,
        filled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        GreenRow(padding = 0, space = 8) {
            Icon(
                painter = painterResource(Res.drawable.magnifying_glass_plus),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(Res.string.id_increase_qr_size),
                style = labelMedium
            )
        }
    }
}

@Composable
fun LearnMoreButton(
    color: Color = green,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GreenIconButton(
        text = stringResource(Res.string.id_learn_more),
        icon = painterResource(Res.drawable.arrow_square_out),
        color = color,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun AboutButton(onClick: () -> Unit) {
    GreenIconButton(
        text = stringResource(Res.string.id_about),
        icon = painterResource(Res.drawable.info),
        color = whiteMedium,
        onClick = onClick
    )
}

@Composable
fun BiometricsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    MmdButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).widthIn(min = 48.dp),
        enabled = true,
        filled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.id_biometrics),
            style = labelMedium
        )
    }
}

@Composable
fun RichWatchOnlyButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    MmdButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).widthIn(min = 48.dp),
        enabled = true,
        filled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = "RWO",
            style = labelMedium
        )
    }
}

@Composable
fun AppSettingsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    MmdButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).widthIn(min = 48.dp),
        enabled = true,
        filled = false,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.id_app_settings),
            style = labelMedium
        )
    }
}
