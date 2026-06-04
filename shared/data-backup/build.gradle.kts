import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
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
                implementation(project(":shared:data-diary"))
                implementation(project(":shared:data-summary"))
                implementation(project(":shared:domain-backup"))
                implementation(project(":shared:domain-diary"))
                implementation(project(":shared:domain-summary"))
                implementation(libs.coroutines.core)
                implementation(libs.datetime)
                implementation(libs.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
            }
        }
    }
}

android {
    namespace = "com.jeong.sumdiary.data.backup"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}
