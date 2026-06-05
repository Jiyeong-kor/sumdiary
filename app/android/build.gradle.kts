import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val releaseSigningStoreFile = providers.gradleProperty("sumdiary.android.signing.storeFile")
val releaseSigningStorePassword = providers.gradleProperty("sumdiary.android.signing.storePassword")
val releaseSigningKeyAlias = providers.gradleProperty("sumdiary.android.signing.keyAlias")
val releaseSigningKeyPassword = providers.gradleProperty("sumdiary.android.signing.keyPassword")
val hasReleaseSigningConfig = listOf(
    releaseSigningStoreFile,
    releaseSigningStorePassword,
    releaseSigningKeyAlias,
    releaseSigningKeyPassword
).all { it.orNull?.isNotBlank() == true }

android {
    namespace = "com.jeong.sumdiary.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jeong.sumdiary.android"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        resValue(
            type = "string",
            name = "google_drive_oauth_client_id",
            value = providers.gradleProperty("sumdiary.googleDriveOAuthClientId").orElse("").get()
        )
    }

    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = file(releaseSigningStoreFile.get())
                storePassword = releaseSigningStorePassword.get()
                keyAlias = releaseSigningKeyAlias.get()
                keyPassword = releaseSigningKeyPassword.get()
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
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
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.datetime)
    implementation(libs.hilt.android)
    implementation(libs.play.services.auth)
    ksp(libs.hilt.compiler)
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
