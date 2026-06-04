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
    val LightBackground = Color(0xFFF7F6F2)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceSubtle = Color(0xFFEFEEE8)
    val LightSurfacePressed = Color(0xFFE7E5DD)
    val LightTextPrimary = Color(0xFF1B1B18)
    val LightTextSecondary = Color(0xFF686862)
    val LightTextTertiary = Color(0xFF929087)
    val LightBorder = Color(0xFFD8D6CD)
    val LightBorderStrong = Color(0xFFB9B6AA)
    val LightAccent = Color(0xFF4E6757)
    val LightAccentSubtle = Color(0xFFDDE7DF)
    val LightWarning = Color(0xFF8A5A22)
    val LightWarningSubtle = Color(0xFFF1E5D2)
    val LightDanger = Color(0xFFA6423A)
    val LightDangerSubtle = Color(0xFFF0DAD7)
    val LightSuccess = Color(0xFF3F6B4F)
    val LightInfo = Color(0xFF4D6473)

    val DarkBackground = Color(0xFF151512)
    val DarkSurface = Color(0xFF20201C)
    val DarkSurfaceSubtle = Color(0xFF2A2924)
    val DarkSurfacePressed = Color(0xFF333128)
    val DarkTextPrimary = Color(0xFFF0EEE7)
    val DarkTextSecondary = Color(0xFFB9B6AA)
    val DarkTextTertiary = Color(0xFF87847A)
    val DarkBorder = Color(0xFF3B3931)
    val DarkBorderStrong = Color(0xFF5B584E)
    val DarkAccent = Color(0xFFA8C4AF)
    val DarkAccentSubtle = Color(0xFF2D3B32)
    val DarkWarning = Color(0xFFD4A45F)
    val DarkWarningSubtle = Color(0xFF3D2F1D)
    val DarkDanger = Color(0xFFDF8A82)
    val DarkDangerSubtle = Color(0xFF422723)
    val DarkSuccess = Color(0xFF91C29C)
    val DarkInfo = Color(0xFF9FB7C4)
}

object SumDiarySpacing {
    val None = 0.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Section = 32.dp
    val Onboarding = 40.dp
}

object SumDiaryRadii {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
}

private val BaseTypography = Typography()

private val SumDiaryTypography = Typography(
    displaySmall = BaseTypography.displaySmall.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = BaseTypography.titleLarge.copy(
        fontSize = 22.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = BaseTypography.titleMedium.copy(
        fontSize = 18.sp,
        lineHeight = 26.sp,
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
    extraLarge = RoundedCornerShape(SumDiaryRadii.Lg)
)

private val SumDiaryLightColorScheme = lightColorScheme(
    primary = SumDiaryColors.LightAccent,
    onPrimary = Color.White,
    primaryContainer = SumDiaryColors.LightAccentSubtle,
    onPrimaryContainer = SumDiaryColors.LightTextPrimary,
    secondary = SumDiaryColors.LightInfo,
    onSecondary = Color.White,
    secondaryContainer = SumDiaryColors.LightSurfaceSubtle,
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
    secondary = SumDiaryColors.DarkInfo,
    onSecondary = SumDiaryColors.DarkBackground,
    secondaryContainer = SumDiaryColors.DarkSurfaceSubtle,
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
