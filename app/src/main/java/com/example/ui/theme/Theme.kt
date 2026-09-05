package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    LIGHT,
    DIM,
    DARK
}

private val LightColorScheme = lightColorScheme(
    primary = SleekBlue,
    onPrimary = Color.White,
    primaryContainer = SleekBluePillBg,
    onPrimaryContainer = SleekBluePillText,
    secondary = Emerald600,
    onSecondary = Color.White,
    secondaryContainer = EfficiencyBgLight,
    onSecondaryContainer = Emerald700,
    tertiary = Purple600,
    onTertiary = Color.White,
    tertiaryContainer = UsageBgLight,
    onTertiaryContainer = Purple800,
    background = SleekLightBg,
    onBackground = Slate900,
    surface = SleekLightSurface,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate100,
    error = Rose600,
    onError = Color.White,
    errorContainer = Rose50,
    onErrorContainer = Rose600,
)

private val DimColorScheme = darkColorScheme(
    primary = SleekBlue,
    onPrimary = Color.White,
    primaryContainer = SleekDimSurfaceVariant,
    onPrimaryContainer = SleekBluePillBg,
    secondary = Emerald500,
    onSecondary = Color.Black,
    secondaryContainer = SleekDimEfficiencyBg,
    onSecondaryContainer = Emerald500,
    tertiary = Purple600,
    onTertiary = Color.White,
    tertiaryContainer = SleekDimUsageBg,
    onTertiaryContainer = Color(0xFFD8B4FE),
    background = SleekDimBg,
    onBackground = Color(0xFFF1F5F9),
    surface = SleekDimSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SleekDimSurfaceVariant,
    onSurfaceVariant = Slate400,
    outline = SleekDimBorder,
    outlineVariant = Color(0xFF282E3A),
    error = Rose600,
    onError = Color.White,
    errorContainer = Color(0xFF3B1520),
    onErrorContainer = Color(0xFFFDA4AF),
)

private val DarkColorScheme = darkColorScheme(
    primary = SleekBlue,
    onPrimary = Color.White,
    primaryContainer = SleekDarkSurfaceVariant,
    onPrimaryContainer = SleekBluePillBg,
    secondary = Emerald500,
    onSecondary = Color.Black,
    secondaryContainer = SleekDarkEfficiencyBg,
    onSecondaryContainer = Emerald500,
    tertiary = Purple600,
    onTertiary = Color.White,
    tertiaryContainer = SleekDarkUsageBg,
    onTertiaryContainer = Color(0xFFE9D5FF),
    background = SleekDarkBg,
    onBackground = Color.White,
    surface = SleekDarkSurface,
    onSurface = Color.White,
    surfaceVariant = SleekDarkSurfaceVariant,
    onSurfaceVariant = Slate400,
    outline = SleekDarkBorder,
    outlineVariant = Color(0xFF22252C),
    error = Rose600,
    onError = Color.White,
    errorContainer = Color(0xFF33131A),
    onErrorContainer = Color(0xFFFECDD3),
)

@Composable
fun DishyTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DIM -> DimColorScheme
        AppThemeMode.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

