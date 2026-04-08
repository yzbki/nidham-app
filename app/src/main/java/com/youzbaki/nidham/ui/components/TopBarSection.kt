package com.youzbaki.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.service.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBarSection(
    colorScheme: ColorScheme,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    listData: ListData,
    dataStore: DataStoreManager,
    menuExpanded: Boolean,
    sortMode: String,
    onSortModeChange: (String) -> Unit,
    onMenuExpandChange: (Boolean) -> Unit,
    onShowSaveDialog: () -> Unit,
    onShowLoadDialog: suspend () -> Unit,
    onShowImportDialog: () -> Unit,
    onShowExportDialog: () -> Unit,
    onShowSettings: () -> Unit,
    onShowAbout: () -> Unit,
    onUndo: () -> Unit,
    updateSavedLists: (List<Pair<String, String>>) -> Unit,
    onNewList: () -> Unit
) {
    val context = LocalContext.current
    var sortExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Centered App Title
        Text(
            text = "Nidham",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Undo button
        IconButton(
            onClick = {
                SoundManager.playButton(context)
                onUndo() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Undo")
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort Menu
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = {
                    SoundManager.playButton(context)
                    sortExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        tint = colorScheme.onBackground
                    )
                }
                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false },
                    modifier = Modifier.background(colorScheme.surface)
                ) {
                    listOf(
                        "custom" to "Custom",
                        "unchecked" to "Unchecked",
                        "checked" to "Checked"
                    ).forEach { (mode, label) ->
                        DropdownMenuItem(
                            text = { Text(label, color = colorScheme.onSurface) },
                            modifier = Modifier.background(
                                if (sortMode == mode) colorScheme.onBackground.copy(alpha = 0.1f)
                                else Color.Transparent
                            ),
                            onClick = {
                                SoundManager.playButton(context)
                                onSortModeChange(mode)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }

            // Menu
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = {
                    SoundManager.playButton(context)
                    onMenuExpandChange(true) }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = colorScheme.onBackground
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { onMenuExpandChange(false) },
                    modifier = Modifier.background(colorScheme.surface)
                ) {
                    // New List
                    DropdownMenuItem(
                        text = { Text("New", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.Create, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onMenuExpandChange(false)
                            scope.launch {
                                if (dataStore.canAddNewList()) {
                                    onNewList()
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("New list created!")
                                } else {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Maximum number of lists reached.")
                                }
                            }
                        }
                    )

                    // Save List
                    DropdownMenuItem(
                        text = { Text("Save", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onShowSaveDialog()
                            onMenuExpandChange(false)
                        }
                    )

                    // Load List
                    DropdownMenuItem(
                        text = { Text("Load", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            scope.launch {
                                updateSavedLists(dataStore.getSavedLists())
                                onShowLoadDialog()
                            }
                            onMenuExpandChange(false)
                        }
                    )

                    HorizontalDivider()

                    // Import List
                    DropdownMenuItem(
                        text = { Text("Import", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onShowImportDialog()
                            onMenuExpandChange(false)
                        }
                    )

                    // Export List
                    DropdownMenuItem(
                        text = { Text("Export", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onShowExportDialog()
                            onMenuExpandChange(false)
                        }
                    )

                    HorizontalDivider()

                    // Settings
                    DropdownMenuItem(
                        text = { Text("Settings", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onShowSettings()
                            onMenuExpandChange(false)
                        }
                    )

                    // About
                    DropdownMenuItem(
                        text = { Text("About", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                        onClick = {
                            SoundManager.playButton(context)
                            onShowAbout()
                            onMenuExpandChange(false)
                        }
                    )
                }
            }
        }
    }
}