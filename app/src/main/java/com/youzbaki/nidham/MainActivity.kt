package com.youzbaki.nidham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.youzbaki.nidham.service.AdManager
import com.youzbaki.nidham.ui.screens.ToDoListScreen
import com.youzbaki.nidham.ui.theme.NidhamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Firebase App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
        firebaseAppCheck.setTokenAutoRefreshEnabled(true)

        // Initialize AdManager
        AdManager.initialize(this)

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