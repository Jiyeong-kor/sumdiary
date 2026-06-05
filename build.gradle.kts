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

tasks.register("writeIosReleaseLocalConfig") {
    group = "release"
    description = "Writes the ignored iOS local xcconfig from Gradle release properties."

    val iosClientId = providers.gradleProperty("sumdiary.ios.googleDriveOAuthClientId")
    val iosReversedClientId = providers.gradleProperty("sumdiary.ios.googleDriveOAuthReversedClientId")
    val iosDevelopmentTeam = providers.gradleProperty("sumdiary.ios.developmentTeam")
    val outputFile = layout.projectDirectory.file("app/iosApp/Config/SumDiary.local.xcconfig")

    inputs.property("iosClientId", iosClientId.orElse(""))
    inputs.property("iosReversedClientId", iosReversedClientId.orElse(""))
    inputs.property("iosDevelopmentTeam", iosDevelopmentTeam.orElse(""))
    outputs.file(outputFile)

    doLast {
        val clientId = iosClientId.orElse("").get().trim()
        val reversedClientId = iosReversedClientId.orElse("").get().trim()
        val developmentTeam = iosDevelopmentTeam.orElse("").get().trim()

        if (clientId.isBlank()) {
            throw GradleException("Missing Gradle property: sumdiary.ios.googleDriveOAuthClientId")
        }
        if (reversedClientId.isBlank()) {
            throw GradleException("Missing Gradle property: sumdiary.ios.googleDriveOAuthReversedClientId")
        }
        if (developmentTeam.isBlank()) {
            throw GradleException("Missing Gradle property: sumdiary.ios.developmentTeam")
        }

        val file = outputFile.asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            GOOGLE_DRIVE_IOS_CLIENT_ID = $clientId
            GOOGLE_DRIVE_IOS_REVERSED_CLIENT_ID = $reversedClientId
            SUMDIARY_IOS_DEVELOPMENT_TEAM = $developmentTeam
            """.trimIndent() + System.lineSeparator()
        )
        logger.lifecycle("Wrote ${file.relativeTo(layout.projectDirectory.asFile).invariantSeparatorsPath}")
    }
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

        fun requireToken(path: String, token: String, message: String) {
            if (!read(path).contains(token)) {
                blockers += message
            }
        }

        fun requireGradleProperty(name: String, message: String) {
            if (providers.gradleProperty(name).orElse("").get().isBlank()) {
                blockers += message
            }
        }

        fun requireGradleFileProperty(name: String, missingMessage: String, missingFileMessage: String) {
            val value = providers.gradleProperty(name).orElse("").get().trim()
            if (value.isBlank()) {
                blockers += missingMessage
                return
            }

            if (!file(value).isFile) {
                blockers += missingFileMessage
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
        requireNoToken(
            path = "app/android/src/main/java/com/jeong/sumdiary/android/di/AppContainer.kt",
            token = "UnavailableGoogleDriveAccessTokenProvider",
            message = "Android release path still has no Google Drive OAuth access token provider."
        )
        requireGradleProperty(
            name = "sumdiary.googleDriveOAuthClientId",
            message = "Google Drive OAuth client id Gradle property is missing."
        )
        requireGradleFileProperty(
            name = "sumdiary.android.signing.storeFile",
            missingMessage = "Android release signing storeFile Gradle property is missing.",
            missingFileMessage = "Android release signing storeFile does not point to an existing file."
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
        requireGradleProperty(
            name = "sumdiary.ios.googleDriveOAuthClientId",
            message = "iOS Google Drive OAuth client id Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.ios.googleDriveOAuthReversedClientId",
            message = "iOS Google Drive reversed client id Gradle property is missing."
        )
        requireGradleProperty(
            name = "sumdiary.ios.developmentTeam",
            message = "iOS Apple Developer Team ID Gradle property is missing."
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
            path = "app/android/src/main/java/com/jeong/sumdiary/android/di/AppContainer.kt",
            token = "PlatformSummarizerProvider.create()",
            message = "Android release path still uses the shared local summarizer fallback instead of ML Kit GenAI."
        )
        requireNoToken(
            path = "app/ios/src/commonMain/kotlin/com/jeong/sumdiary/ios/IosAppFactory.kt",
            token = "PlatformSummarizerProvider.create()",
            message = "iOS release path still uses the shared local summarizer fallback instead of Apple Foundation Models."
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
            path = "docs/PLAY_DATA_SAFETY_DRAFT.md",
            message = "Google Play Data safety draft is missing."
        )
        requireFile(
            path = "docs/APP_STORE_PRIVACY_DRAFT.md",
            message = "App Store privacy draft is missing."
        )
        requireFile(
            path = "docs/GOOGLE_OAUTH_VERIFICATION_AUDIT.md",
            message = "Google OAuth verification audit is missing."
        )
        requireFile(
            path = "docs/CONTENT_RATING_DRAFT.md",
            message = "Store content rating draft is missing."
        )
        requireFile(
            path = "app/iosApp/SumDiary.xcodeproj/project.pbxproj",
            message = "iOS Xcode app project is missing."
        )
        requireToken(
            path = "app/iosApp/SumDiary.xcodeproj/project.pbxproj",
            token = "PRODUCT_BUNDLE_IDENTIFIER = com.jeong.sumdiary;",
            message = "iOS bundle identifier is not fixed to com.jeong.sumdiary."
        )
        requireToken(
            path = "app/iosApp/SumDiary.xcodeproj/project.pbxproj",
            token = "MARKETING_VERSION = 1.0;",
            message = "iOS marketing version is not fixed to 1.0."
        )
        requireToken(
            path = "app/iosApp/SumDiary.xcodeproj/project.pbxproj",
            token = "CURRENT_PROJECT_VERSION = 1;",
            message = "iOS build number is not fixed to 1."
        )
        requireFile(
            path = "app/iosApp/SumDiary/GoogleDriveAuthorizationProvider.swift",
            message = "iOS Google Drive OAuth access token provider is missing."
        )
        requireToken(
            path = "app/iosApp/SumDiary/Info.plist",
            token = "<string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>",
            message = "iOS Info.plist does not read CFBundleIdentifier from PRODUCT_BUNDLE_IDENTIFIER."
        )
        requireToken(
            path = "app/iosApp/SumDiary/Info.plist",
            token = "<string>$(MARKETING_VERSION)</string>",
            message = "iOS Info.plist does not read CFBundleShortVersionString from MARKETING_VERSION."
        )
        requireToken(
            path = "app/iosApp/SumDiary/Info.plist",
            token = "<string>$(CURRENT_PROJECT_VERSION)</string>",
            message = "iOS Info.plist does not read CFBundleVersion from CURRENT_PROJECT_VERSION."
        )
        requireFile(
            path = "app/iosApp/Config/SumDiary.xcconfig",
            message = "iOS Xcode build configuration file is missing."
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
