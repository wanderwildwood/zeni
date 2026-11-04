package com.blockstream.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The 16 sp floor (Mudita's review, 4 Nov 2026) that GreenTypography passes through. */
class HouseTypeTest {

    private fun all(t: Typography) = listOf(
        "displayLarge" to t.displayLarge, "displayMedium" to t.displayMedium,
        "displaySmall" to t.displaySmall, "headlineLarge" to t.headlineLarge,
        "headlineMedium" to t.headlineMedium, "headlineSmall" to t.headlineSmall,
        "titleLarge" to t.titleLarge, "titleMedium" to t.titleMedium, "titleSmall" to t.titleSmall,
        "bodyLarge" to t.bodyLarge, "bodyMedium" to t.bodyMedium, "bodySmall" to t.bodySmall,
        "labelLarge" to t.labelLarge, "labelMedium" to t.labelMedium, "labelSmall" to t.labelSmall,
    )

    @Test
    fun nothingIsBelowTheFloor() {
        // Material's own default scale goes down to 11 sp: the floor lifts every step of it.
        for ((name, style) in all(raiseToFloor(Typography()))) {
            assertTrue("$name is ${style.fontSize}", style.fontSize.value >= HOUSE_TYPE_FLOOR_SP)
        }
    }

    @Test
    fun largerStylesAreUntouched() {
        val base = Typography()
        val raised = raiseToFloor(base)
        for ((name, style) in all(base)) {
            if (style.fontSize.value >= HOUSE_TYPE_FLOOR_SP) assertEquals(name, style, all(raised).toMap().getValue(name))
        }
    }

    @Test
    fun onlyTheSizeChanges() {
        val base = Typography()
        val small = raiseToFloor(base).labelSmall
        assertEquals(16.sp, small.fontSize)
        assertEquals(base.labelSmall.copy(fontSize = 16.sp), small)
    }

    @Test
    fun theFloorClearsMuditas24pxOnTheKompakt() {
        // 213 dpi, a Lato line 1.2 times its size.
        val px = HOUSE_TYPE_FLOOR_SP * 213f / 160f * 1.2f
        assertTrue("$px px", px >= 24f)
    }
}
