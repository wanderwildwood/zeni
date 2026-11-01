package com.blockstream.compose.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmWindowTest {
    @Test
    fun unarmedFirstPressArms() {
        assertEquals(ArmWindow.Press.Arm, ArmWindow.press(null, 1_000))
    }

    @Test
    fun secondPressInsideTheWindowFires() {
        assertEquals(ArmWindow.Press.Fire, ArmWindow.press(1_000, 1_000 + ArmWindow.MS - 1))
    }

    @Test
    fun secondPressAfterTheWindowArmsAgain() {
        assertEquals(ArmWindow.Press.Arm, ArmWindow.press(1_000, 1_000 + ArmWindow.MS))
    }

    @Test
    fun windowIsSixSecondsAndAClockGoingBackwardsNeverArms() {
        assertEquals(6000L, ArmWindow.MS)
        assertFalse(ArmWindow.isArmed(5_000, 4_000))
        assertTrue(ArmWindow.isArmed(5_000, 5_000))
    }
}

class PageStepTest {
    @Test
    fun pageIsTheViewportLessTenPercentAndNeverZero() {
        assertEquals(900f, pageStep(1000), 0.001f)
        assertEquals(1f, pageStep(0), 0.001f)
    }
}
