// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false // ✅ Add KSP at the project level
    id("com.google.dagger.hilt.android") version "2.51" apply false
    alias(libs.plugins.google.gms.google.services) apply false // ✅ Declare Hilt Plugin
}