package com.jeong.sumdiary.core.util

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.name
import kotlin.test.Test
import kotlin.test.assertTrue

class SensitiveLogOutputGuardTest {
    @Test
    fun productSourcesDoNotUseDirectLoggingOrCrashBreadcrumbApis() {
        val projectRoot = locateProjectRoot()
        val violations = ProductSourceRoots
            .map { path -> projectRoot.resolve(path) }
            .flatMap { root -> root.productSourceFiles() }
            .filterNot { path -> path.invariantSeparatorsPathString.endsWith(AllowedLoggerBoundary) }
            .flatMap { sourceFile -> sourceFile.forbiddenLogTokenViolations(projectRoot) }

        assertTrue(
            actual = violations.isEmpty(),
            message = buildString {
                appendLine("제품 코드에서 직접 로그/크래시 breadcrumb API를 사용하지 않아야 합니다.")
                appendLine("일기 원문, 요약문, OAuth token, 백업 비밀번호, 암호화 키가 로그에 남을 수 있습니다.")
                violations.forEach { violation -> appendLine("- $violation") }
            }
        )
    }

    private fun locateProjectRoot(): Path {
        var cursor = Path.of("").toAbsolutePath()
        while (!Files.exists(cursor.resolve("settings.gradle.kts"))) {
            cursor = cursor.parent ?: error("프로젝트 루트를 찾지 못했습니다.")
        }
        return cursor
    }

    private fun Path.productSourceFiles(): List<Path> {
        if (!Files.exists(this)) {
            return emptyList()
        }
        return Files.walk(this).use { paths ->
            paths
                .filter { path ->
                    Files.isRegularFile(path) &&
                        path.extension in ProductSourceExtensions &&
                        path.invariantSeparatorsPathString.contains("/src/") &&
                        path.invariantSeparatorsPathString.contains("Main/")
                }
                .toList()
        }
    }

    private fun Path.forbiddenLogTokenViolations(projectRoot: Path): List<String> {
        val content = String(Files.readAllBytes(this))
        val relativePath = projectRoot.relativize(this).invariantSeparatorsPathString
        return ForbiddenLogTokens
            .filter { token -> content.contains(token) }
            .map { token -> "$relativePath: $token" }
    }

    private companion object {
        const val AllowedLoggerBoundary =
            "shared/core-util/src/commonMain/kotlin/com/jeong/sumdiary/core/util/NapierLogger.kt"

        val ProductSourceRoots = listOf(
            "app",
            "shared"
        )

        val ProductSourceExtensions = setOf(
            "kt",
            "java",
            "swift"
        )

        val ForbiddenLogTokens = listOf(
            "println(",
            "printStackTrace(",
            "android.util.Log",
            "Log.d(",
            "Log.i(",
            "Log.w(",
            "Log.e(",
            "FirebaseCrashlytics",
            "Crashlytics",
            "recordException(",
            "setCustomKey(",
            "breadcrumb",
            "Napier."
        )
    }
}
