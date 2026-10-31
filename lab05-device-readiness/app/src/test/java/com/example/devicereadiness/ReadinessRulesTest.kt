package com.example.devicereadiness

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Local JVM tests for the rules shared by the UI and the tests (Task 2 / T3 / T4). */
class ReadinessRulesTest {

    @Test
    fun deviceName_mustNotBeBlankAfterTrimming() {
        assertFalse(isDeviceNameValid(""))
        assertFalse(isDeviceNameValid("   "))
        assertTrue(isDeviceNameValid("Sensor-A"))
        assertTrue(isDeviceNameValid("  Sensor-A  "))
    }

    @Test
    fun battery_acceptsWholeNumbersFromZeroToOneHundred() {
        assertEquals(0, parseBattery("0"))
        assertEquals(29, parseBattery("29"))
        assertEquals(30, parseBattery("30"))
        assertEquals(100, parseBattery("100"))
        assertEquals(68, parseBattery(" 68 "))
    }

    @Test
    fun battery_rejectsMissingNonNumericAndOutOfRangeInput() {
        assertNull(parseBattery(""))
        assertNull(parseBattery("abc"))
        assertNull(parseBattery("12.5"))
        assertNull(parseBattery("-1"))
        assertNull(parseBattery("101"))
    }

    @Test
    fun ready_requiresAtLeastThirtyPercentAndNetwork() {
        assertFalse(isReady(0, networkAvailable = true))
        assertFalse(isReady(29, networkAvailable = true))
        assertTrue(isReady(30, networkAvailable = true))
        assertTrue(isReady(100, networkAvailable = true))
        assertFalse(isReady(80, networkAvailable = false))
    }
}
