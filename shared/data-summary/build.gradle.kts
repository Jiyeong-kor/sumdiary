plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting
    }
}

android {
    namespace = "com.jeong.sumdiary.data.summary"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
