package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Switch
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton

@Composable
actual fun MmdSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier, enabled: Boolean) {
    Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
}

@Composable
actual fun MmdCheckbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier, enabled: Boolean) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
}

@Composable
actual fun MmdRadioButton(selected: Boolean, onClick: (() -> Unit)?, modifier: Modifier, enabled: Boolean) {
    RadioButton(selected = selected, onClick = onClick, modifier = modifier, enabled = enabled)
}
