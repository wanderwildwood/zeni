package com.blockstream.compose.screens.settings.components

import com.blockstream.compose.components.MmdSwitch
import com.blockstream.compose.components.MmdCheckbox
import com.blockstream.compose.components.MmdRadioButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.blockstream.compose.utils.appTestTag

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    subtitleMaxLines: Int = 2
) {
    SettingsItem(
        title = title,
        subtitle = subtitle,
        // The row is what a screen reader lands on, so it says it is a switch and whether it is on.
        modifier = modifier.semantics {
            role = Role.Switch
            toggleableState = ToggleableState(checked)
        },
        subtitleMaxLines = subtitleMaxLines,
        // The house switch: the whole row takes the press, the switch only shows the state.
        onClick = { onCheckedChange(!checked) },
        rightContent = {
            MmdSwitch(
                checked = checked,
                onCheckedChange = null,
                modifier = modifier.appTestTag(testTag)
            )
        }
    )
}