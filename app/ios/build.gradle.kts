plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "SumDiary"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:feature-summary"))
                implementation(project(":shared:feature-entry"))
                implementation(project(":shared:data-diary"))
                implementation(project(":shared:data-summary"))
                implementation(project(":shared:domain-diary"))
                implementation(project(":shared:domain-summary"))
                implementation(project(":shared:core-util"))
                implementation(libs.sqldelight.driver.native)
            }
        }
        val commonTest by getting
    }
}
