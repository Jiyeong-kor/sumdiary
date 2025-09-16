import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(project(":shared:core-util"))
                implementation(project(":shared:domain-diary"))
            }
        }
        val commonTest by getting
    }
}

android {
    namespace = "com.jeong.sumdiary.feature.entry"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
