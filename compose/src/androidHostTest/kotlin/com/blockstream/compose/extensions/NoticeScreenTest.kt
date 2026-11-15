package com.blockstream.compose.extensions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoticeScreenTest {
    @Test fun aNoticePostedWithAStepBackStaysOnTheNewScreen() {
        // The step back runs first; the notice then goes up on the screen it led to.
        assertFalse(noticeLeftBehind(shownOn = "check-page", nowOn = "check-page"))
    }

    @Test fun leavingTheScreenClosesItsNotice() {
        assertTrue(noticeLeftBehind(shownOn = "pin", nowOn = "home"))
    }

    @Test fun noNoticeYetMeansNothingToClose() {
        assertFalse(noticeLeftBehind(shownOn = null, nowOn = null))
    }
}
