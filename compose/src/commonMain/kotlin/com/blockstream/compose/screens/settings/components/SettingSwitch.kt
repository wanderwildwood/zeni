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
        modifier = modifier,
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