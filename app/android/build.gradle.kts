plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.jeong.sumdiary.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jeong.sumdiary"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
