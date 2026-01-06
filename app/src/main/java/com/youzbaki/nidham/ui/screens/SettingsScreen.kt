package com.youzbaki.nidham.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.DataStoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    themeMode: String,
    colorVariant: String,
    showLabels: Boolean,
    textFieldSquared: Boolean,
    onThemeModeChange: (String) -> Unit,
    onColorVariantChange: (String) -> Unit,
    onShowLabelsChange: (Boolean) -> Unit,
    onShapeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }
    var showRestoreDefaultsConfirm by remember { mutableStateOf(false) }

    // Default Settings
    val DEFAULT_THEME_MODE = "system"
    val DEFAULT_COLOR_VARIANT = "default"
    val DEFAULT_SHOW_LABELS = true
    val DEFAULT_TEXTFIELD_SQUARED = true

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
                            imageVector = Icons.Default.Home,
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "Settings",
                        style = typography.headlineMedium.copy(
                            fontFamily = FontFamily.SansSerif,
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
                    var colorSchemeExpanded by remember { mutableStateOf(false) }
                    val colors = listOf("default", "blue", "green", "red", "orange", "yellow", "purple", "pink")
                    var selectedColor by remember { mutableStateOf(colorVariant) }

                    Text(
                        text = "Color Scheme",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = colorSchemeExpanded,
                        onExpandedChange = { colorSchemeExpanded = !colorSchemeExpanded }
                    ) {
                        TextField(
                            value = selectedColor.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = if (showLabels) {
                                { Text("Select Color Scheme",
                                    color = colorScheme.onSurface.copy(alpha = 0.7f)) }
                            } else null,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorSchemeExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface,
                                focusedContainerColor = colorScheme.surface,
                                unfocusedContainerColor = colorScheme.surface,
                                cursorColor = colorScheme.onSurface,
                                focusedIndicatorColor = colorScheme.primary,
                                unfocusedIndicatorColor = colorScheme.onSurface.copy(alpha = 0.4f),
                                focusedLabelColor = colorScheme.primary,
                                unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = colorSchemeExpanded,
                            onDismissRequest = { colorSchemeExpanded = false },
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            colors.forEach { colorName ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            colorName.replaceFirstChar { it.uppercase() },
                                            color = colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedColor = colorName
                                        onColorVariantChange(colorName)
                                        scope.launch {
                                            dataStore.saveColorVariant(colorName)
                                        }
                                        colorSchemeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Theme",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("system", "dark", "light").forEach { mode ->
                            Button(
                                onClick = {
                                    onThemeModeChange(mode)
                                    scope.launch {
                                        dataStore.saveThemeMode(mode)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeMode == mode) colorScheme.onSurfaceVariant
                                    else colorScheme.surface,
                                    contentColor   = if (themeMode == mode) colorScheme.surfaceVariant
                                    else colorScheme.onSurface
                                )
                            ) {
                                Text(mode.capitalize())
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Labels",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(true to "Enabled", false to "Disabled").forEach { (value, label) ->
                            Button(
                                onClick = {
                                    onShowLabelsChange(value)
                                    scope.launch {
                                        dataStore.saveShowLabels(value)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (showLabels == value)
                                            colorScheme.onSurfaceVariant
                                        else colorScheme.surface,
                                    contentColor =
                                        if (showLabels == value)
                                            colorScheme.surfaceVariant
                                        else colorScheme.onSurface
                                )
                            ) {
                                Text(label)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "List Shape",
                        style = typography.titleMedium,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(true to "Squared", false to "Rounded").forEach { (value, label) ->
                            Button(
                                onClick = {
                                    onShapeChange(value)
                                    scope.launch {
                                        dataStore.saveTextFieldShape(value)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (textFieldSquared == value)
                                            colorScheme.onSurfaceVariant
                                        else colorScheme.surface,
                                    contentColor =
                                        if (textFieldSquared == value)
                                            colorScheme.surfaceVariant
                                        else colorScheme.onSurface
                                )
                            ) {
                                Text(label)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showRestoreDefaultsConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "Restore System Defaults",
                            style = typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (showRestoreDefaultsConfirm) {
                        AlertDialog(
                            onDismissRequest = { showRestoreDefaultsConfirm = false },
                            title = {
                                Text(
                                    text = "Restore Defaults",
                                    color = colorScheme.onBackground
                                )
                            },
                            text = {
                                Text(
                                    text = "Are you sure you want to restore system defaults? This will reset all settings.",
                                    color = colorScheme.onSurface
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        // Update UI state
                                        onThemeModeChange("system")
                                        onColorVariantChange("default")
                                        onShowLabelsChange(true)
                                        onShapeChange(true)
                                        selectedColor = "default"

                                        // Persist
                                        scope.launch {
                                            dataStore.saveThemeMode("system")
                                            dataStore.saveColorVariant("default")
                                            dataStore.saveShowLabels(true)
                                            dataStore.saveTextFieldShape(true)
                                        }

                                        showRestoreDefaultsConfirm = false
                                    }
                                ) {
                                    Text("Restore", color = colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showRestoreDefaultsConfirm = false }
                                ) {
                                    Text("Cancel", color = colorScheme.onBackground)
                                }
                            },
                            containerColor = colorScheme.background
                        )
                    }
                }
            }
        }
    }
}