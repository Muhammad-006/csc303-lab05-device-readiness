# CSC303 Lab 05 - Device Readiness Checker (Jetpack Compose + Material 3)

Package: `com.example.devicereadiness` - Kotlin, Compose, Material 3.
Student: Muhammad Bilal - FA23-BCE-062

## 1. Validation and Readiness Rules
The UI (`DeviceReadinessApp`) and the tests use the same constants from `ReadinessRules.kt`.

| Field or decision | Rule |
|-------------------|------|
| Device name | Required after trimming; blank input is invalid. |
| Battery percentage | A whole number from 0 through 100; missing, non-numeric and out-of-range input is invalid. |
| Network available | Boolean selected through a Material Switch. |
| Ready | Valid form, battery at least 30 and network available. |
| Needs attention | Valid form but battery below 30 or network unavailable. |
| After input changes | Hide the previous result so it cannot describe stale values. |

## 2. State and event map
| State | Displayed by | Changed by | Saved with |
|-------|--------------|------------|------------|
| `deviceName` | Device name field, result card | `onDeviceNameChange`, Clear | `rememberSaveable` |
| `batteryText` | Battery field, error text, result card | `onBatteryChange`, Clear | `rememberSaveable` |
| `networkAvailable` | Switch and readiness decision | `onNetworkChange`, Clear | `rememberSaveable` |
| `submitted` | Error visibility and result visibility | Evaluate (true); any edit and Clear (false) | `rememberSaveable` |

## 3. Tests
- Local JVM: `.\gradlew.bat testDebugUnitTest` (`ReadinessRulesTest` - boundaries 0, 29, 30, 100, -1, 101).
- Instrumented (emulator/device): `.\gradlew.bat connectedDebugAndroidTest` (`DeviceReadinessScreenTest` - 2 tests).

## 4. Build configuration
AGP 9.4.0, Gradle 9.6.0, Kotlin 2.4.21, Compose BOM 2026.09.00, compileSdk/targetSdk 36, minSdk 26.

## 5. Evidence (fill in after running)
| Item | Value |
|------|-------|
| Device / API level | |
| Git commit ID | |
