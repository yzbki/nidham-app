package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nidham.data.DataStoreManager
import com.example.nidham.data.ListData
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
    onMenuExpandChange: (Boolean) -> Unit,
    onShowSaveDialog: () -> Unit,
    onShowLoadDialog: suspend () -> Unit,
    onShowSettings: () -> Unit,
    updateSavedLists: (List<Pair<String, String>>) -> Unit,
    onNewList: () -> Unit
) {
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

        // Quicksave button
        IconButton(
            onClick = {
                scope.launch {
                    if (listData.isTitleValid(listData.title.value, dataStore)) {
                        dataStore.saveListData(listData)
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar("List saved as \"${listData.title.value}\"")
                    } else {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            "ERROR: Invalid title."
                        )
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Quicksave")
        }

        // Menu
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { onMenuExpandChange(true) }) {
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
                            onNewList()
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("New list created!")
                            }
                            onMenuExpandChange(false)
                        }
                    )

                    // Save List
                    DropdownMenuItem(
                        text = { Text("Save", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) },
                        onClick = {
                            onShowSaveDialog()
                            onMenuExpandChange(false)
                        }
                    )

                    // Load List
                    DropdownMenuItem(
                        text = { Text("Load", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                        onClick = {
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
                            // TO:DO Import functionality
                        }
                    )

                    // Export List
                    DropdownMenuItem(
                        text = { Text("Export", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = null) },
                        onClick = {
                            // TO:DO Export functionality
                        }
                    )

                    HorizontalDivider()

                    // Settings
                    DropdownMenuItem(
                        text = { Text("Settings", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        onClick = {
                            onMenuExpandChange(false)
                            onShowSettings()
                        }
                    )

                    // About
                    DropdownMenuItem(
                        text = { Text("About", color = colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                        onClick = {
                            // TO:DO About page
                        }
                    )
                }
            }
        }
    }
}