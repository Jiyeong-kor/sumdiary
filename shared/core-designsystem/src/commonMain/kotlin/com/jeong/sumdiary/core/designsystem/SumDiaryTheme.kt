package com.jeong.sumdiary.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun SumDiaryTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colors,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}

@Composable
fun SumDiaryTheme(content: @Composable () -> Unit) {
    SumDiaryTheme(darkTheme = false, content = content)
}
