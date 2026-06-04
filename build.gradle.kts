plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.sqldelight) apply false
}

tasks.register("checkReleaseReadiness") {
    group = "verification"
    description = "Audits release blockers that must be cleared before store submission."

    val strictMode = providers.gradleProperty("sumdiary.releaseReadiness.strict")
        .map(String::toBoolean)
        .orElse(false)

    doLast {
        val blockers = mutableListOf<String>()
        val rootDir = layout.projectDirectory.asFile

        fun read(path: String): String {
            val file = rootDir.resolve(path)
            return if (file.exists()) file.readText() else ""
        }

        fun requireNoToken(path: String, token: String, message: String) {
            if (read(path).contains(token)) {
                blockers += message
            }
        }

        fun requireFile(path: String, message: String) {
            if (!rootDir.resolve(path).isFile) {
                blockers += message
            }
        }

        fun requireGradleProperty(name: String, message: String) {
            if (providers.gradleProperty(name).orElse("").get().isBlank()) {
                blockers += message
            }
        }

        requireNoToken(
            path = "app/android/src/main/java/com/jeong/sumdiary/android/di/AppContainer.kt",
            token = "InMemoryBackupCloudRepository",
            message = "Android release path still wires the development in-memory backup cloud repository."
        )
        requireNoToken(
            path = "app/android/src/main/java/com/jeong/sumdiary/android/di/AppContainer.kt",
            token = "UnconfiguredGoogleDriveBackupCloudRepository",
            message = "Android release path still uses an unconfigured Google Drive backup cloud repository."
        )
        requireGradleProperty(
            name = "sumdiary.googleDriveOAuthClientId",
            message = "Google Drive OAuth client id Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.android.signing.storeFile",
            message = "Android release signing storeFile Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.android.signing.storePassword",
            message = "Android release signing storePassword Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.android.signing.keyAlias",
            message = "Android release signing keyAlias Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.android.signing.keyPassword",
            message = "Android release signing keyPassword Gradle property is missing."
        )
        requireNoToken(
            path = "app/android/src/main/java/com/jeong/sumdiary/android/di/AppContainer.kt",
            token = "DevelopmentBackupSnapshotRepository",
            message = "Android release path still wires the development backup snapshot repository."
        )
        requireNoToken(
            path = "shared/data-summary/src/commonMain/kotlin/com/jeong/sumdiary/data/summary/PlatformSummarizerProvider.kt",
            token = "UnsupportedSummarizerEngine",
            message = "Default summarizer provider still returns the unsupported placeholder engine."
        )
        requireNoToken(
            path = "shared/data-summary/src/commonMain/kotlin/com/jeong/sumdiary/data/summary/PlatformSummarizerProvider.kt",
            token = "LocalSummarizerEngine",
            message = "Default summarizer provider still uses the local fallback instead of platform on-device AI SDKs."
        )
        requireFile(
            path = "docs/PRIVACY_POLICY.md",
            message = "Final privacy policy document is missing."
        )
        requireFile(
            path = "docs/TERMS_OF_USE.md",
            message = "Final terms of use document is missing."
        )
        requireFile(
            path = "docs/STORE_SUBMISSION_CHECKLIST.md",
            message = "Store submission checklist is missing."
        )
        requireFile(
            path = "app/iosApp/SumDiary.xcodeproj/project.pbxproj",
            message = "iOS Xcode app project is missing."
        )

        if (blockers.isEmpty()) {
            logger.lifecycle("Release readiness audit passed. No blockers found.")
            return@doLast
        }

        logger.lifecycle("Release readiness audit found ${blockers.size} blocker(s):")
        blockers.forEachIndexed { index, blocker ->
            logger.lifecycle("${index + 1}. $blocker")
        }

        if (strictMode.get()) {
            throw GradleException(
                "Release readiness strict mode failed with ${blockers.size} blocker(s)."
            )
        }

        logger.lifecycle(
            "Audit mode completed. Run with -Psumdiary.releaseReadiness.strict=true before store submission."
        )
    }
}
