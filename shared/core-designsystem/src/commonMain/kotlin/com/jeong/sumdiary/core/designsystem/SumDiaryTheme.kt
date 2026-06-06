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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object SumDiaryColors {
    val lightBackground = Color(0xFFFAFAFA)
    val lightBackgroundPaper = Color(0xFFF7F6F2)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceSubtle = Color(0xFFF5F7FA)
    val lightSurfacePressed = Color(0xFFEEF2FA)
    val lightTextPrimary = Color(0xFF1E1E1E)
    val lightTextSecondary = Color(0xFF686868)
    val lightTextTertiary = Color(0xFF9A9A9A)
    val lightBorder = Color(0xFFE5E7EB)
    val lightBorderStrong = Color(0xFFA9C0F7)
    val lightAccent = Color(0xFF4F7CF3)
    val lightAccentSubtle = Color(0xFFDDE8FF)
    val lightSecondary = Color(0xFFA9C0F7)
    val lightSecondarySubtle = Color(0xFFEDF3FF)
    val lightWarning = Color(0xFFD9A441)
    val lightWarningSubtle = Color(0xFFFFF4D6)
    val lightDanger = Color(0xFFE64B4B)
    val lightDangerSubtle = Color(0xFFFFE4E4)
    val lightSuccess = Color(0xFF4CAF7B)
    val lightInfo = Color(0xFF4F7CF3)

    val darkBackground = Color(0xFF111317)
    val darkSurface = Color(0xFF1B1D22)
    val darkSurfaceSubtle = Color(0xFF242832)
    val darkSurfacePressed = Color(0xFF2A2E38)
    val darkTextPrimary = Color(0xFFF4F5F7)
    val darkTextSecondary = Color(0xFFC4C7CE)
    val darkTextTertiary = Color(0xFF8E929B)
    val darkBorder = Color(0xFF343844)
    val darkBorderStrong = Color(0xFF5B668A)
    val darkAccent = Color(0xFF9AB5FF)
    val darkAccentSubtle = Color(0xFF263A70)
    val darkSecondary = Color(0xFFA9C0F7)
    val darkSecondarySubtle = Color(0xFF273246)
    val darkWarning = Color(0xFFE0B85F)
    val darkWarningSubtle = Color(0xFF3D3218)
    val darkDanger = Color(0xFFFF8B86)
    val darkDangerSubtle = Color(0xFF4A2427)
    val darkSuccess = Color(0xFF80C79F)
    val darkInfo = Color(0xFF9AB5FF)
}

object SumDiarySpacing {
    val none = 0.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val section = 40.dp
    val onboarding = 40.dp
    val screenGutter = 24.dp
    val cardGap = 12.dp
    val listGap = 8.dp
}

object SumDiaryRadii {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val sheet = 28.dp
}

internal expect val sumDiaryFontFamily: FontFamily

private val baseTypography = Typography()

private fun TextStyle.sumDiaryTextStyle(
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontWeight: FontWeight
): TextStyle =
    copy(
        fontFamily = sumDiaryFontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontWeight = fontWeight
    )

private val sumDiaryTypography = Typography(
    displayLarge = baseTypography.displayLarge.sumDiaryTextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    displayMedium = baseTypography.displayMedium.sumDiaryTextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    displaySmall = baseTypography.displaySmall.sumDiaryTextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineLarge = baseTypography.headlineLarge.sumDiaryTextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = baseTypography.headlineMedium.sumDiaryTextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = baseTypography.headlineSmall.sumDiaryTextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = baseTypography.titleLarge.sumDiaryTextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = baseTypography.titleMedium.sumDiaryTextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleSmall = baseTypography.titleSmall.sumDiaryTextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = baseTypography.bodyLarge.sumDiaryTextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = baseTypography.bodyMedium.sumDiaryTextStyle(
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = baseTypography.bodySmall.sumDiaryTextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = baseTypography.labelLarge.sumDiaryTextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    labelMedium = baseTypography.labelMedium.sumDiaryTextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = baseTypography.labelSmall.sumDiaryTextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium
    )
)

private val sumDiaryShapes = Shapes(
    extraSmall = RoundedCornerShape(SumDiaryRadii.xs),
    small = RoundedCornerShape(SumDiaryRadii.sm),
    medium = RoundedCornerShape(SumDiaryRadii.md),
    large = RoundedCornerShape(SumDiaryRadii.lg),
    extraLarge = RoundedCornerShape(SumDiaryRadii.xxl)
)

private val sumDiaryLightColorScheme = lightColorScheme(
    primary = SumDiaryColors.lightAccent,
    onPrimary = Color.White,
    primaryContainer = SumDiaryColors.lightAccentSubtle,
    onPrimaryContainer = SumDiaryColors.lightAccent,
    secondary = SumDiaryColors.lightSecondary,
    onSecondary = SumDiaryColors.lightTextPrimary,
    secondaryContainer = SumDiaryColors.lightSecondarySubtle,
    onSecondaryContainer = SumDiaryColors.lightTextPrimary,
    background = SumDiaryColors.lightBackground,
    onBackground = SumDiaryColors.lightTextPrimary,
    surface = SumDiaryColors.lightSurface,
    onSurface = SumDiaryColors.lightTextPrimary,
    surfaceVariant = SumDiaryColors.lightSurfaceSubtle,
    onSurfaceVariant = SumDiaryColors.lightTextSecondary,
    outline = SumDiaryColors.lightBorder,
    outlineVariant = SumDiaryColors.lightBorderStrong,
    error = SumDiaryColors.lightDanger,
    onError = Color.White,
    errorContainer = SumDiaryColors.lightDangerSubtle,
    onErrorContainer = SumDiaryColors.lightTextPrimary
)

private val sumDiaryDarkColorScheme = darkColorScheme(
    primary = SumDiaryColors.darkAccent,
    onPrimary = SumDiaryColors.darkBackground,
    primaryContainer = SumDiaryColors.darkAccentSubtle,
    onPrimaryContainer = SumDiaryColors.darkTextPrimary,
    secondary = SumDiaryColors.darkSecondary,
    onSecondary = SumDiaryColors.darkBackground,
    secondaryContainer = SumDiaryColors.darkSecondarySubtle,
    onSecondaryContainer = SumDiaryColors.darkTextPrimary,
    background = SumDiaryColors.darkBackground,
    onBackground = SumDiaryColors.darkTextPrimary,
    surface = SumDiaryColors.darkSurface,
    onSurface = SumDiaryColors.darkTextPrimary,
    surfaceVariant = SumDiaryColors.darkSurfaceSubtle,
    onSurfaceVariant = SumDiaryColors.darkTextSecondary,
    outline = SumDiaryColors.darkBorder,
    outlineVariant = SumDiaryColors.darkBorderStrong,
    error = SumDiaryColors.darkDanger,
    onError = SumDiaryColors.darkBackground,
    errorContainer = SumDiaryColors.darkDangerSubtle,
    onErrorContainer = SumDiaryColors.darkTextPrimary
)

@Composable
fun SumDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) sumDiaryDarkColorScheme else sumDiaryLightColorScheme,
        typography = sumDiaryTypography,
        shapes = sumDiaryShapes,
        content = content
    )
}
