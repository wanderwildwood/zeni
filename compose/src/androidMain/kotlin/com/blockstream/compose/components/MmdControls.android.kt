package com.blockstream.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mudita.mmd.components.switcher.SwitchMMD
import com.mudita.mmd.components.checkbox.CheckboxMMD
import com.mudita.mmd.components.radio_button.RadioButtonMMD

@Composable
actual fun MmdSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier, enabled: Boolean) {
    SwitchMMD(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
}

@Composable
actual fun MmdCheckbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, modifier: Modifier, enabled: Boolean) {
    CheckboxMMD(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled)
}

@Composable
actual fun MmdRadioButton(selected: Boolean, onClick: (() -> Unit)?, modifier: Modifier, enabled: Boolean) {
    RadioButtonMMD(selected = selected, onClick = onClick, modifier = modifier, enabled = enabled)
}
