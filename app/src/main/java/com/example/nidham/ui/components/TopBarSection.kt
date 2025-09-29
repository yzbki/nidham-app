package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nidham.DataStoreManager
import com.example.nidham.ListData
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

        // Menu aligned to the end
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
                        onClick = {
                            onShowSaveDialog()
                            onMenuExpandChange(false)
                        }
                    )

                    // Load List
                    DropdownMenuItem(
                        text = { Text("Load", color = colorScheme.onSurface) },
                        onClick = {
                            scope.launch {
                                updateSavedLists(dataStore.getSavedLists())
                                onShowLoadDialog()
                            }
                            onMenuExpandChange(false)
                        }
                    )
                }
            }
        }
    }
}