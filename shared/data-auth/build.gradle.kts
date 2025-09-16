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
                implementation(project(":shared:domain-auth"))
                implementation(libs.coroutines.core)
            }
        }
        val commonTest by getting
    }
}

android {
    namespace = "com.jeong.sumdiary.data.auth"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
