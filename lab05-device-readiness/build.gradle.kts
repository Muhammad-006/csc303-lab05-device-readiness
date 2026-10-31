// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Print every unit-test name and its PASSED/FAILED result in the terminal
// (useful evidence for the lab report).
subprojects {
    tasks.withType<Test>().configureEach {
        testLogging {
            events("passed", "failed", "skipped")
            showStackTraces = true
        }
    }
}
