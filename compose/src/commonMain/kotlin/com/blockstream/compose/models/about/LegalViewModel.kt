package com.blockstream.compose.models.about

import androidx.lifecycle.viewModelScope
import com.blockstream.compose.models.GreenViewModel
import com.blockstream.compose.navigation.NavData
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/** Backs the Privacy, Terms and Licences pages: no wallet, no login, just a title. */
class LegalViewModel(private val name: String, private val title: StringResource) : GreenViewModel() {
    override fun screenName(): String = name

    override val isLoginRequired: Boolean = false

    init {
        viewModelScope.launch {
            _navData.value = NavData(title = getString(title))
        }
        bootstrap()
    }
}
