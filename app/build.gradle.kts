plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // ✅ Use alias for KSP
    id("com.google.dagger.hilt.android") // ✅ Apply Hilt plugin in app module
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.example.ecommerceapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ecommerceapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM (Manages versions automatically)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // ✅ Hilt Dependency Injection (Using KSP instead of KAPT)
    implementation(libs.dagger.hilt.android) // Hilt Core
    ksp(libs.hilt.android.compiler) // Hilt Compiler (KSP)
    implementation(libs.androidx.hilt.navigation.compose) // Hilt for Jetpack Compose

    // ✅ Coil (Image Loading for Jetpack Compose)
    implementation(libs.coil.compose)

    // ✅ Navigation Compose (Jetpack Navigation Component)
    implementation(libs.androidx.navigation.compose)

    // ✅ Kotlinx Serialization (For JSON Parsing)
    implementation(libs.kotlinx.serialization.json)

    // ✅ Accompanist Pager (For ViewPager in Jetpack Compose)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // ✅ Razorpay (Payment Gateway Integration)
    implementation(libs.checkout)

    // ✅ Custom Bottom Navigation Bar (Animated Bottom Navigation)
    implementation(libs.bottombar)
}
