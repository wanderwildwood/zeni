package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The three small controls in the E Ink look. On Android they are MMD's `SwitchMMD`, `CheckboxMMD`
 * and `RadioButtonMMD`; on desktop and iOS, which have no MMD, the Material ones. A null callback
 * draws the state and leaves the press to the row around it.
 */
@Composable
expect fun MmdSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier = Modifier, enabled: Boolean = true)

@Composable
expect fun MmdCheckbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier = Modifier, enabled: Boolean = true)

@Composable
expect fun MmdRadioButton(selected: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier, enabled: Boolean = true)

/** A quiet action in the E Ink look: MMD offers no text button, so it is the outlined one. */
@Composable
fun MmdTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    MmdButton(
        onClick = onClick,
        modifier = modifier.androidx_heightIn48(),
        enabled = enabled,
        filled = false,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
        content = content,
    )
}

private fun Modifier.androidx_heightIn48(): Modifier = this.heightIn(min = 48.dp)

/**
 * A choice of one among a few, as a row of equal house buttons: the chosen one filled, the rest
 * outlined. (Material's segmented button is a rounded pill with a tick; this is the same press
 * the rest of the app uses.)
 */
@Composable
fun MmdSegmented(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            MmdButton(
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                enabled = true,
                filled = index == selectedIndex,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
            ) {
                androidx.compose.material3.Text(label, maxLines = 1)
            }
        }
    }
}
