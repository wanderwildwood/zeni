package com.blockstream.compose.models.about

import androidx.lifecycle.viewModelScope
import blockstream_green.common.generated.resources.Res
import blockstream_green.common.generated.resources.id_about
import blockstream_green.common.generated.resources.id_thank_you_for_your_feedback
import com.blockstream.compose.events.Event
import com.blockstream.compose.events.Events
import com.blockstream.compose.extensions.launchIn
import com.blockstream.compose.models.GreenViewModel
import com.blockstream.compose.navigation.NavData
import com.blockstream.compose.screens.about.contactUri
import com.blockstream.compose.sideeffects.OpenBrowserType
import com.blockstream.compose.sideeffects.SideEffects
import com.blockstream.compose.utils.StringHolder
import com.blockstream.data.extensions.isNotBlank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

abstract class AboutViewModelAbstract : GreenViewModel() {
    abstract val year: String
    abstract val version: String

    abstract val rate: MutableStateFlow<Int>

    abstract val email: MutableStateFlow<String>

    abstract val feedback: MutableStateFlow<String>
}

class AboutViewModel : AboutViewModelAbstract() {

    override fun screenName(): String = "About"

    override val isLoginRequired: Boolean = false

    override val rate: MutableStateFlow<Int> = MutableStateFlow(0)
    override val email: MutableStateFlow<String> = MutableStateFlow("")
    override val feedback: MutableStateFlow<String> = MutableStateFlow("")

    class LocalEvents {
        /** Opens the person's email app with a blank message to the developer; nothing is attached. */
        class ClickContact(subject: String) :
            Events.OpenBrowser(contactUri(subject), OpenBrowserType.OPEN_SYSTEM)

        object SendFeedback : Event
    }

    override val year: String =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year.toString()

    override val version = appInfo.version

    init {
        combine(rate, feedback) { rate, feedback ->
            rate > 0 && feedback.isNotBlank()
        }.onEach {
            _isValid.value = it
        }.launchIn(this)

        viewModelScope.launch {
            _navData.value = NavData(title = getString(Res.string.id_about))
        }

        bootstrap()
    }

    override suspend fun handleEvent(event: Event) {
        super.handleEvent(event)

        if (event is LocalEvents.SendFeedback) {
            countly.recordFeedback(
                rating = rate.value,
                email = email.value.trim(),
                comment = feedback.value
            )
            postSideEffect(SideEffects.Snackbar(StringHolder.create(Res.string.id_thank_you_for_your_feedback)))
            postSideEffect(SideEffects.Dismiss)

            rate.value = 0
            email.value = ""
            feedback.value = ""
        }
    }
}

class AboutViewModelPreview(override val year: String, override val version: String) :
    AboutViewModelAbstract() {

    override val rate: MutableStateFlow<Int> = MutableStateFlow(0)
    override val email: MutableStateFlow<String> = MutableStateFlow("")
    override val feedback: MutableStateFlow<String> = MutableStateFlow("")

    companion object {
        fun preview() = AboutViewModelPreview(year = "2000", version = "4.0.0-preview")
    }
}
