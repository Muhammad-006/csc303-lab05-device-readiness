package com.example.devicereadiness

/*
 * Task 2 - the validation and readiness rules, written once so that the UI and the
 * tests use exactly the same boundaries.
 *
 *  Device name  : required after trimming; blank input is invalid.
 *  Battery      : whole number 0..100; missing, non-numeric and out-of-range input is invalid.
 *  Ready        : valid form AND battery >= 30 AND network available.
 *  Needs attention : valid form but battery < 30 or network unavailable.
 */

const val MIN_BATTERY = 0
const val MAX_BATTERY = 100
const val READY_BATTERY_THRESHOLD = 30

fun isDeviceNameValid(name: String): Boolean = name.trim().isNotEmpty()

/** Returns the battery value, or null when the text is empty, not a whole number or out of range. */
fun parseBattery(text: String): Int? =
    text.trim().toIntOrNull()?.takeIf { it in MIN_BATTERY..MAX_BATTERY }

/** Only call this with a battery value that parseBattery() accepted. */
fun isReady(battery: Int, networkAvailable: Boolean): Boolean =
    battery >= READY_BATTERY_THRESHOLD && networkAvailable
