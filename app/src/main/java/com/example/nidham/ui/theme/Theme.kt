package com.example.nidham.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0, 71, 171),
    secondary = Color.White,
    tertiary = Color.LightGray,

    background = Color.DarkGray,
    surface = Color.LightGray,
    onBackground = Color.White,
    onSurface = Color.White,
    inverseSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0, 71, 171),
    secondary = Color.White,
    tertiary = Color.LightGray,

    background = Color.LightGray,
    surface = Color.DarkGray,
    onBackground = Color.White,
    onSurface = Color.White,
    inverseSurface = Color.White
)

@Composable
fun NidhamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}