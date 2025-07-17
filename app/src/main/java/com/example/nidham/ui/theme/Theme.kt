package com.example.nidham.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color


private val DarkColorScheme = darkColorScheme(
    primary = Color(0, 74, 127),
    secondary = Color.White,
    tertiary = Color.LightGray,

    background = Color(0, 74, 127),
    surface = Color.LightGray,
    onBackground = Color.White,
    onSurface = Color.Black,
    inverseSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0, 74, 127),
    secondary = Color.White,
    tertiary = Color.LightGray,

    background = Color(0, 74, 127),
    surface = Color.LightGray,
    onBackground = Color.White,
    onSurface = Color.Black,
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