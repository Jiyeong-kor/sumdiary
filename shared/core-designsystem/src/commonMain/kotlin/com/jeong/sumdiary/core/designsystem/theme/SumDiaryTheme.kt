package com.jeong.sumdiary.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 앱 공통 테마
 */
@Composable
fun SumDiaryTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

val LocalIsDarkTheme = staticCompositionLocalOf { false }

private val lightScheme = lightColorScheme(
    primary = Color(0xFF4E8AFA),
    secondary = Color(0xFF4DB6AC),
    tertiary = Color(0xFFFFB74D)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFFFCC80)
)
