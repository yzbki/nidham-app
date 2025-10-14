package com.example.nidham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.nidham.ui.screens.ToDoListScreen
import com.example.nidham.ui.theme.NidhamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            var themeMode by remember { mutableStateOf("system") }
            var colorVariant by remember { mutableStateOf("default") }

            NidhamTheme(
                themeMode = themeMode,
                colorVariant = colorVariant
            ) {
                ToDoListScreen(
                    themeMode = themeMode,
                    colorVariant = colorVariant,
                    onThemeChange = { newMode, newVariant ->
                        if (newMode.isNotEmpty()) themeMode = newMode
                        if (newVariant.isNotEmpty()) colorVariant = newVariant
                    }
                )
            }
        }
    }
}