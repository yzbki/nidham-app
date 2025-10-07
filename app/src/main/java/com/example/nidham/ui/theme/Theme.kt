package com.example.nidham.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = MediumGray,
    onPrimary = Color.White,
    secondary = DarkGray,
    onSecondary = Color.White,
    tertiary = LightGray,
    onTertiary = Color.White,
    background = LightGray,
    onBackground = DarkGray,
    surface = MediumGray,
    onSurface = DarkGray,
    inverseSurface = DarkGray
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkSurface,
    onPrimary = Color.White,
    secondary = LightText,
    onSecondary = Color.White,
    tertiary = DarkBackground,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    inverseSurface = LightText
)

private val PinkLightColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63),
    onPrimary = Color.White,
    secondary = Color(0xFFFFA6C9),
    onSecondary = Color.White,
    tertiary = Color(0xFFF8BBD0),
    onTertiary = Color.Black,
    background = Color(0xFFFCE4EC),
    onBackground = Color(0xFFE91E63),
    surface = Color(0xFFF8BBD0),
    onSurface = Color(0xFFE91E63),
    inverseSurface = Color.White
)

private val PinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF06292),
    onPrimary = Color.White,
    secondary = Color(0xFFFFA6C9),
    onSecondary = Color.Black,
    tertiary = Color(0xFFF8BBD0),
    onTertiary = Color.Black,
    background = Color(0xFF2B0013),
    onBackground = Color(0xFFFFC1E3),
    surface = Color(0xFF3B0020),
    onSurface = Color(0xFFFFC1E3),
    inverseSurface = Color.White
)

private val GreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color.Black,
    tertiary = Color(0xFFC8E6C9),
    onTertiary = Color.Black,
    background = Color(0xFFE8F5E9),
    onBackground = Color(0xFF2E7D32),
    surface = Color(0xFFC8E6C9),
    onSurface = Color(0xFF2E7D32),
    inverseSurface = Color.White
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),
    onPrimary = Color.Black,
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color.Black,
    tertiary = Color(0xFFC8E6C9),
    onTertiary = Color.Black,
    background = Color(0xFF0F1A12),
    onBackground = Color(0xFFD0E8D0),
    surface = Color(0xFF1B2A1F),
    onSurface = Color(0xFFD0E8D0),
    inverseSurface = Color.White
)

private val BlueLightColorScheme = lightColorScheme(
    primary = Color(0xFF0D47A1),
    onPrimary = Color.White,
    secondary = Color(0xFF90CAF9),
    onSecondary = Color.White,
    tertiary = Color(0xFFBBDEFB),
    onTertiary = Color.Black,
    background = Color(0xFFE3F2FD),
    onBackground = Color(0xFF0D47A1),
    surface = Color(0xFFBBDEFB),
    onSurface = Color(0xFF0D47A1),
    inverseSurface = Color.White
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    secondary = Color(0xFF64B5F6),
    onSecondary = Color.Black,
    tertiary = Color(0xFF90CAF9),
    onTertiary = Color.Black,
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFBBDEFB),
    surface = Color(0xFF1A1F26),
    onSurface = Color(0xFFBBDEFB),
    inverseSurface = Color.White
)

private val OrangeLightColorScheme = lightColorScheme(
    primary = Color(0xFFF57C00),
    onPrimary = Color.White,
    secondary = Color(0xFFFFCC80),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFE0B2),
    onTertiary = Color.Black,
    background = Color(0xFFFFF3E0),
    onBackground = Color(0xFFF57C00),
    surface = Color(0xFFFFE0B2),
    onSurface = Color(0xFFF57C00),
    inverseSurface = Color.White
)

private val OrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFA726),
    onPrimary = Color.Black,
    secondary = Color(0xFFFFCC80),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFE0B2),
    onTertiary = Color.Black,
    background = Color(0xFF2C1300),
    onBackground = Color(0xFFFFE0B2),
    surface = Color(0xFF3B1A00),
    onSurface = Color(0xFFFFE0B2),
    inverseSurface = Color.White
)

private val RedLightColorScheme = lightColorScheme(
    primary = Color(0xFFF44336),
    onPrimary = Color.White,
    secondary = Color(0xFFFFCDD2),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFEBEE),
    onTertiary = Color.Black,
    background = Color(0xFFFFEBEE),
    onBackground = Color(0xFFD32F2F),
    surface = Color(0xFFFFCDD2),
    onSurface = Color(0xFFD32F2F),
    inverseSurface = Color.White
)

private val RedDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD32F2F),
    onPrimary = Color.White,
    secondary = Color(0xFFFFCDD2),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFEBEE),
    onTertiary = Color.Black,
    background = Color(0xFF3B0000),
    onBackground = Color(0xFFFFCDD2),
    surface = Color(0xFFB71C1C),
    onSurface = Color(0xFFFFCDD2),
    inverseSurface = Color.White
)

private val YellowLightColorScheme = lightColorScheme(
    primary = Color(0xFFFFC107),
    onPrimary = Color.Black,
    secondary = Color(0xFFFFE082),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFF59D),
    onTertiary = Color.Black,
    background = Color(0xFFFFFDE7),
    onBackground = Color(0xFFFFC107),
    surface = Color(0xFFFFE082),
    onSurface = Color(0xFFFFC107),
    inverseSurface = Color.White
)

private val YellowDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFA000),
    onPrimary = Color.Black,
    secondary = Color(0xFFFFE082),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFF59D),
    onTertiary = Color.Black,
    background = Color(0xFF332500),
    onBackground = Color(0xFFFFE082),
    surface = Color(0xFFFF8F00),
    onSurface = Color(0xFFFFE082),
    inverseSurface = Color.White
)

private val PurpleLightColorScheme = lightColorScheme(
    primary = Color(0xFF9C27B0),
    onPrimary = Color.White,
    secondary = Color(0xFFE1BEE7),
    onSecondary = Color.Black,
    tertiary = Color(0xFFD1C4E9),
    onTertiary = Color.Black,
    background = Color(0xFFF3E5F5),
    onBackground = Color(0xFF9C27B0),
    surface = Color(0xFFD1C4E9),
    onSurface = Color(0xFF9C27B0),
    inverseSurface = Color.White
)

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color.White,
    secondary = Color(0xFFCE93D8),
    onSecondary = Color.Black,
    tertiary = Color(0xFFB39DDB),
    onTertiary = Color.Black,
    background = Color(0xFF2A003D),
    onBackground = Color(0xFFCE93D8),
    surface = Color(0xFF4A148C),
    onSurface = Color(0xFFCE93D8),
    inverseSurface = Color.White
)

private val LimeLightColorScheme = lightColorScheme(
    primary = Color(0xFFCDDC39),
    onPrimary = Color.Black,
    secondary = Color(0xFFF0F4C3),
    onSecondary = Color.Black,
    tertiary = Color(0xFFE6EE9C),
    onTertiary = Color.Black,
    background = Color(0xFFF9FBE7),
    onBackground = Color(0xFF827717),
    surface = Color(0xFFE6EE9C),
    onSurface = Color(0xFF827717),
    inverseSurface = Color.White
)

private val LimeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFAFB42B),
    onPrimary = Color.Black,
    secondary = Color(0xFFCCEA6A),
    onSecondary = Color.Black,
    tertiary = Color(0xFFDCE775),
    onTertiary = Color.Black,
    background = Color(0xFF1B1F00),
    onBackground = Color(0xFFDCE775),
    surface = Color(0xFF3C3F00),
    onSurface = Color(0xFFDCE775),
    inverseSurface = Color.White
)

@Composable
fun NidhamTheme(
    themeMode: String = "system", // "light", "dark", or "system"
    colorVariant: String = "default", // "default", "pink", "blue", etc.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when (colorVariant) {
        "pink" -> if (darkTheme) PinkDarkColorScheme else PinkLightColorScheme
        "blue" -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        "green" -> if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        "orange" -> if (darkTheme) OrangeDarkColorScheme else OrangeLightColorScheme
        "red" -> if (darkTheme) RedDarkColorScheme else RedLightColorScheme
        "yellow" -> if (darkTheme) YellowDarkColorScheme else YellowLightColorScheme
        "purple" -> if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
        "lime" -> if (darkTheme) LimeDarkColorScheme else LimeLightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.background.toArgb()

        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = !darkTheme
        controller.isAppearanceLightNavigationBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}