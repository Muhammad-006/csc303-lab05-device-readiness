package com.example.devicereadiness

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.devicereadiness.ui.theme.DeviceReadinessTheme
import org.junit.Rule
import org.junit.Test

/**
 * Task 11 - instrumented Compose UI tests (they need an emulator or a device).
 * Nodes are found with test tags (stable) while the visible labels stay meaningful.
 * Command:  .\gradlew.bat connectedDebugAndroidTest
 */
class DeviceReadinessScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun text(resId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)

    @Test
    fun blankForm_showsBothErrors_andNoResultCard() {
        composeRule.setContent { DeviceReadinessTheme { DeviceReadinessApp() } }

        composeRule.onNodeWithTag("evaluate").performClick()

        composeRule.onNodeWithText(text(R.string.error_device_name_required)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.error_battery_invalid)).assertIsDisplayed()
        composeRule.onNodeWithTag("result_card").assertDoesNotExist()
    }

    @Test
    fun validForm_showsReadyResultCard() {
        composeRule.setContent { DeviceReadinessTheme { DeviceReadinessApp() } }

        composeRule.onNodeWithTag("device_name").performTextInput("Sensor-A")
        composeRule.onNodeWithTag("battery").performTextInput("80")
        composeRule.onNodeWithTag("evaluate").performClick()

        composeRule.onNodeWithTag("result_card").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.status_ready)).assertIsDisplayed()
    }
}
