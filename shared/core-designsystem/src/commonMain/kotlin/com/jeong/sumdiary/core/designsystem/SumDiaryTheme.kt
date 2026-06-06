package com.jeong.sumdiary.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object SumDiaryColors {
    val LightBackground = Color(0xFFFAFAFA)
    val LightBackgroundPaper = Color(0xFFF7F6F2)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceSubtle = Color(0xFFF5F7FA)
    val LightSurfacePressed = Color(0xFFEEF2FA)
    val LightTextPrimary = Color(0xFF1E1E1E)
    val LightTextSecondary = Color(0xFF686868)
    val LightTextTertiary = Color(0xFF9A9A9A)
    val LightBorder = Color(0xFFE5E7EB)
    val LightBorderStrong = Color(0xFFA9C0F7)
    val LightAccent = Color(0xFF4F7CF3)
    val LightAccentSubtle = Color(0xFFDDE8FF)
    val LightSecondary = Color(0xFFA9C0F7)
    val LightSecondarySubtle = Color(0xFFEDF3FF)
    val LightWarning = Color(0xFFD9A441)
    val LightWarningSubtle = Color(0xFFFFF4D6)
    val LightDanger = Color(0xFFE64B4B)
    val LightDangerSubtle = Color(0xFFFFE4E4)
    val LightSuccess = Color(0xFF4CAF7B)
    val LightInfo = Color(0xFF4F7CF3)

    val DarkBackground = Color(0xFF111317)
    val DarkSurface = Color(0xFF1B1D22)
    val DarkSurfaceSubtle = Color(0xFF242832)
    val DarkSurfacePressed = Color(0xFF2A2E38)
    val DarkTextPrimary = Color(0xFFF4F5F7)
    val DarkTextSecondary = Color(0xFFC4C7CE)
    val DarkTextTertiary = Color(0xFF8E929B)
    val DarkBorder = Color(0xFF343844)
    val DarkBorderStrong = Color(0xFF5B668A)
    val DarkAccent = Color(0xFF9AB5FF)
    val DarkAccentSubtle = Color(0xFF263A70)
    val DarkSecondary = Color(0xFFA9C0F7)
    val DarkSecondarySubtle = Color(0xFF273246)
    val DarkWarning = Color(0xFFE0B85F)
    val DarkWarningSubtle = Color(0xFF3D3218)
    val DarkDanger = Color(0xFFFF8B86)
    val DarkDangerSubtle = Color(0xFF4A2427)
    val DarkSuccess = Color(0xFF80C79F)
    val DarkInfo = Color(0xFF9AB5FF)
}

object SumDiarySpacing {
    val None = 0.dp
    val Xxs = 4.dp
    val Xs = 8.dp
    val Sm = 12.dp
    val Md = 16.dp
    val Lg = 20.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Section = 40.dp
    val Onboarding = 40.dp
    val ScreenGutter = 24.dp
    val CardGap = 12.dp
    val ListGap = 8.dp
}

object SumDiaryRadii {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Sheet = 28.dp
}

private val BaseTypography = Typography()

private val SumDiaryTypography = Typography(
    displayLarge = BaseTypography.displayLarge.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    displaySmall = BaseTypography.displaySmall.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineLarge = BaseTypography.headlineLarge.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = BaseTypography.titleLarge.copy(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = BaseTypography.titleMedium.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = BaseTypography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = BaseTypography.bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = BaseTypography.labelLarge.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = BaseTypography.labelMedium.copy(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = BaseTypography.labelSmall.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium
    )
)

private val SumDiaryShapes = Shapes(
    extraSmall = RoundedCornerShape(SumDiaryRadii.Xs),
    small = RoundedCornerShape(SumDiaryRadii.Sm),
    medium = RoundedCornerShape(SumDiaryRadii.Md),
    large = RoundedCornerShape(SumDiaryRadii.Lg),
    extraLarge = RoundedCornerShape(SumDiaryRadii.Xxl)
)

private val SumDiaryLightColorScheme = lightColorScheme(
    primary = SumDiaryColors.LightAccent,
    onPrimary = Color.White,
    primaryContainer = SumDiaryColors.LightAccentSubtle,
    onPrimaryContainer = SumDiaryColors.LightAccent,
    secondary = SumDiaryColors.LightSecondary,
    onSecondary = SumDiaryColors.LightTextPrimary,
    secondaryContainer = SumDiaryColors.LightSecondarySubtle,
    onSecondaryContainer = SumDiaryColors.LightTextPrimary,
    background = SumDiaryColors.LightBackground,
    onBackground = SumDiaryColors.LightTextPrimary,
    surface = SumDiaryColors.LightSurface,
    onSurface = SumDiaryColors.LightTextPrimary,
    surfaceVariant = SumDiaryColors.LightSurfaceSubtle,
    onSurfaceVariant = SumDiaryColors.LightTextSecondary,
    outline = SumDiaryColors.LightBorder,
    outlineVariant = SumDiaryColors.LightBorderStrong,
    error = SumDiaryColors.LightDanger,
    onError = Color.White,
    errorContainer = SumDiaryColors.LightDangerSubtle,
    onErrorContainer = SumDiaryColors.LightTextPrimary
)

private val SumDiaryDarkColorScheme = darkColorScheme(
    primary = SumDiaryColors.DarkAccent,
    onPrimary = SumDiaryColors.DarkBackground,
    primaryContainer = SumDiaryColors.DarkAccentSubtle,
    onPrimaryContainer = SumDiaryColors.DarkTextPrimary,
    secondary = SumDiaryColors.DarkSecondary,
    onSecondary = SumDiaryColors.DarkBackground,
    secondaryContainer = SumDiaryColors.DarkSecondarySubtle,
    onSecondaryContainer = SumDiaryColors.DarkTextPrimary,
    background = SumDiaryColors.DarkBackground,
    onBackground = SumDiaryColors.DarkTextPrimary,
    surface = SumDiaryColors.DarkSurface,
    onSurface = SumDiaryColors.DarkTextPrimary,
    surfaceVariant = SumDiaryColors.DarkSurfaceSubtle,
    onSurfaceVariant = SumDiaryColors.DarkTextSecondary,
    outline = SumDiaryColors.DarkBorder,
    outlineVariant = SumDiaryColors.DarkBorderStrong,
    error = SumDiaryColors.DarkDanger,
    onError = SumDiaryColors.DarkBackground,
    errorContainer = SumDiaryColors.DarkDangerSubtle,
    onErrorContainer = SumDiaryColors.DarkTextPrimary
)

@Composable
fun SumDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SumDiaryDarkColorScheme else SumDiaryLightColorScheme,
        typography = SumDiaryTypography,
        shapes = SumDiaryShapes,
        content = content
    )
}
