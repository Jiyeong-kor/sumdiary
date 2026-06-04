import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.jeong.sumdiary.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jeong.sumdiary.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "META-INF/{AL2.0,LGPL2.1}"
        jniLibs.keepDebugSymbols += "**/libandroidx.graphics.path.so"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.datetime)
    implementation(libs.sqldelight.driver.android)
    implementation(project(":shared:core-designsystem"))
    implementation(project(":shared:core-util"))
    implementation(project(":shared:data-diary"))
    implementation(project(":shared:data-backup"))
    implementation(project(":shared:data-summary"))
    implementation(project(":shared:domain-diary"))
    implementation(project(":shared:domain-backup"))
    implementation(project(":shared:domain-summary"))
    implementation(project(":shared:feature-entry"))
    implementation(project(":shared:feature-backup"))
    implementation(project(":shared:feature-summary"))
}
