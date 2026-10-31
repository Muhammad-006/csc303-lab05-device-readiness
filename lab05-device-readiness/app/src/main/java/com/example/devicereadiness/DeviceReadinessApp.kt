package com.example.devicereadiness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.devicereadiness.ui.theme.DeviceReadinessTheme

/**
 * Task 4 - the stateful route. It owns the four small UI values and survives
 * rotation because they are stored with rememberSaveable. The child screen only
 * receives current values and event callbacks (state hoisting), so the data flows
 * down and the events flow up (unidirectional data flow).
 */
@Composable
fun DeviceReadinessApp() {
    var deviceName by rememberSaveable { mutableStateOf("") }
    var batteryText by rememberSaveable { mutableStateOf("") }
    var networkAvailable by rememberSaveable { mutableStateOf(true) }
    var submitted by rememberSaveable { mutableStateOf(false) }

    // Derived values: recalculated from the four state values, never stored separately.
    val battery = parseBattery(batteryText)
    val nameError = submitted && !isDeviceNameValid(deviceName)
    val batteryError = submitted && battery == null
    val resultReady: Boolean? =
        if (submitted && !nameError && battery != null) isReady(battery, networkAvailable) else null

    DeviceReadinessScreen(
        deviceName = deviceName,
        batteryText = batteryText,
        networkAvailable = networkAvailable,
        nameError = nameError,
        batteryError = batteryError,
        resultReady = resultReady,
        // Any edit hides the previous result so it can never describe stale values.
        onDeviceNameChange = { deviceName = it; submitted = false },
        onBatteryChange = { batteryText = it; submitted = false },
        onNetworkChange = { networkAvailable = it; submitted = false },
        onEvaluate = { submitted = true },
        onClear = {
            deviceName = ""
            batteryText = ""
            networkAvailable = true
            submitted = false
        }
    )
}

/**
 * Task 5-7 - the stateless Material 3 screen. No remember state is created here for the
 * hoisted values, which makes the screen easy to preview and to test.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceReadinessScreen(
    deviceName: String,
    batteryText: String,
    networkAvailable: Boolean,
    nameError: Boolean,
    batteryError: Boolean,
    resultReady: Boolean?,
    onDeviceNameChange: (String) -> Unit,
    onBatteryChange: (String) -> Unit,
    onNetworkChange: (Boolean) -> Unit,
    onEvaluate: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nameErrorText = stringResource(R.string.error_device_name_required)
    val batteryErrorText = stringResource(R.string.error_battery_invalid)
    val batterySupportText = stringResource(R.string.battery_support)
    val networkDescription = stringResource(R.string.network_switch_description)

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.screen_title)) }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.screen_intro))

            // Task 6 - validated text fields. The label stays visible; the error is TEXT
            // (not only colour) and is also exposed through the semantics error property.
            OutlinedTextField(
                value = deviceName,
                onValueChange = onDeviceNameChange,
                label = { Text(stringResource(R.string.device_name_label)) },
                isError = nameError,
                supportingText = { if (nameError) Text(nameErrorText) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_name")
                    .semantics { if (nameError) error(nameErrorText) }
            )

            OutlinedTextField(
                value = batteryText,
                onValueChange = onBatteryChange,
                label = { Text(stringResource(R.string.battery_label)) },
                isError = batteryError,
                supportingText = { Text(if (batteryError) batteryErrorText else batterySupportText) },
                // The keyboard type is only a hint; parseBattery() still rejects bad input.
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("battery")
                    .semantics { if (batteryError) error(batteryErrorText) }
            )

            // Task 7 - labelled switch row and the two Material actions.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.network_label))
                Switch(
                    checked = networkAvailable,
                    onCheckedChange = onNetworkChange,
                    modifier = Modifier
                        .testTag("network_switch")
                        .semantics { contentDescription = networkDescription }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onEvaluate, modifier = Modifier.testTag("evaluate")) {
                    Text(stringResource(R.string.evaluate))
                }
                OutlinedButton(onClick = onClear, modifier = Modifier.testTag("clear")) {
                    Text(stringResource(R.string.clear))
                }
            }

            // Task 8 - show the card only when there is a result AND the battery value is valid.
            val battery = parseBattery(batteryText)
            if (resultReady != null && battery != null) {
                ReadinessResultCard(
                    deviceName = deviceName.trim(),
                    battery = battery,
                    networkAvailable = networkAvailable,
                    ready = resultReady
                )
            }
        }
    }
}

/**
 * Task 8 - accessible result. The status is written as words (not only colour); the whole
 * card is announced as one sentence built from a formatted string resource.
 */
@Composable
fun ReadinessResultCard(
    deviceName: String,
    battery: Int,
    networkAvailable: Boolean,
    ready: Boolean,
    modifier: Modifier = Modifier
) {
    val status = stringResource(if (ready) R.string.status_ready else R.string.status_attention)
    val message = stringResource(
        if (ready) R.string.result_ready else R.string.result_attention,
        deviceName
    )
    val network = stringResource(
        if (networkAvailable) R.string.network_available else R.string.network_unavailable
    )
    val summary = stringResource(R.string.result_accessibility, deviceName, battery, network, status)
    val progressDescription = stringResource(R.string.battery_progress_description, battery)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_card")
            .semantics(mergeDescendants = true) {
                contentDescription = summary
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(status, style = MaterialTheme.typography.titleLarge)
            Text(message)
            LinearProgressIndicator(
                progress = { battery / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = progressDescription }
            )
        }
    }
}

// Task 9 - previews use empty lambdas because they do not change state.

@Preview(showBackground = true)
@Composable
fun DeviceReadinessEmptyPreview() {
    DeviceReadinessTheme {
        DeviceReadinessScreen(
            deviceName = "",
            batteryText = "",
            networkAvailable = true,
            nameError = false,
            batteryError = false,
            resultReady = null,
            onDeviceNameChange = {},
            onBatteryChange = {},
            onNetworkChange = {},
            onEvaluate = {},
            onClear = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceReadinessReadyPreview() {
    DeviceReadinessTheme {
        DeviceReadinessScreen(
            deviceName = "Sensor-A",
            batteryText = "68",
            networkAvailable = true,
            nameError = false,
            batteryError = false,
            resultReady = true,
            onDeviceNameChange = {},
            onBatteryChange = {},
            onNetworkChange = {},
            onEvaluate = {},
            onClear = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceReadinessErrorPreview() {
    DeviceReadinessTheme {
        DeviceReadinessScreen(
            deviceName = "",
            batteryText = "",
            networkAvailable = true,
            nameError = true,
            batteryError = true,
            resultReady = null,
            onDeviceNameChange = {},
            onBatteryChange = {},
            onNetworkChange = {},
            onEvaluate = {},
            onClear = {}
        )
    }
}
