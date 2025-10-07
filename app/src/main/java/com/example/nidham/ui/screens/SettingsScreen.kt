package com.example.nidham.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    themeMode: String,
    colorVariant: String,
    onThemeModeChange: (String) -> Unit,
    onColorVariantChange: (String) -> Unit
) {
    BackHandler {
        onBackClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = colorScheme.onBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "Settings",
                        style = typography.headlineLarge.copy(
                            fontFamily = FontFamily.Cursive,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Theme",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("dark", "light", "system").forEach { mode ->
                            Button(
                                onClick = { onThemeModeChange(mode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeMode == mode) colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                    else colorScheme.surface,
                                    contentColor   = if (themeMode == mode) colorScheme.surfaceVariant.copy(alpha = 0.75f)
                                    else colorScheme.onSurface
                                )
                            ) {
                                Text(mode.capitalize())
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Color Scheme",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val colors = listOf("default", "red", "orange", "yellow", "green", "lime", "blue", "purple", "pink")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        colors.chunked(3).forEach { rowColors ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowColors.forEach { colorName ->
                                    Button(
                                        onClick = { onColorVariantChange(colorName) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (colorVariant == colorName)
                                                colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                                            else colorScheme.surface,
                                            contentColor = if (colorVariant == colorName)
                                                colorScheme.surfaceVariant.copy(alpha = 0.85f)
                                            else colorScheme.onSurface
                                        )
                                    ) {
                                        Text(colorName.capitalize())
                                    }
                                }
                                if (rowColors.size < 3) {
                                    repeat(3 - rowColors.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}