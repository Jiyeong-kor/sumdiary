package com.jeong.sumdiary.data.summary

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.name
import kotlin.test.Test
import kotlin.test.assertTrue

class SummaryNoExternalNetworkGuardTest {
    @Test
    fun dataSummarySourcesDoNotUseExternalNetworkClients() {
        val moduleRoot = locateModuleRoot()
        val sourceRoots = listOf(
            moduleRoot.resolve("src/commonMain"),
            moduleRoot.resolve("src/androidMain"),
            moduleRoot.resolve("src/iosMain")
        )
        val violations = sourceRoots
            .flatMap { root -> root.kotlinSourceFiles() }
            .flatMap { sourceFile -> sourceFile.networkTokenViolations() }

        assertTrue(
            actual = violations.isEmpty(),
            message = buildString {
                appendLine("요약 모듈은 일기 원문을 외부 AI 서버로 전송하지 않아야 합니다.")
                appendLine("네트워크 클라이언트나 외부 URL 사용이 감지됐습니다:")
                violations.forEach { violation -> appendLine("- $violation") }
            }
        )
    }

    private fun locateModuleRoot(): Path {
        var cursor = Path.of("").toAbsolutePath()
        while (cursor.name != "data-summary") {
            cursor = cursor.parent
                ?: error("shared/data-summary module root를 찾지 못했습니다.")
        }
        return cursor
    }

    private fun Path.kotlinSourceFiles(): List<Path> {
        if (!Files.exists(this)) {
            return emptyList()
        }
        return Files.walk(this).use { paths ->
            paths
                .filter { path -> Files.isRegularFile(path) && path.extension == "kt" }
                .toList()
        }
    }

    private fun Path.networkTokenViolations(): List<String> {
        val content = String(Files.readAllBytes(this))
        return ForbiddenNetworkTokens
            .filter { token -> content.contains(token) }
            .map { token -> "${invariantSeparatorsPathString}: $token" }
    }

    private companion object {
        val ForbiddenNetworkTokens = listOf(
            "io.ktor.client",
            "okhttp3",
            "HttpClient",
            "URLSession",
            "NSURLSession",
            "openConnection",
            "java.net.URL",
            "https://",
            "http://"
        )
    }
}
