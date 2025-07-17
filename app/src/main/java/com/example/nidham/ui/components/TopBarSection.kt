package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
    updateSavedListNames: (List<String>) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Centered App Title
        Text(
            text = "NIDHAM",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )

        // Grouped dropdown menus aligned to the end
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort Dropdown
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { sortMenuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Sort Tasks")
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier.background(colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by Checked", color = colorScheme.onSurface) },
                        onClick = {
                            sortMenuExpanded = false
                            val combined = listData.tasks.zip(listData.checkedStates)
                                .sortedByDescending { it.second }
                            listData.tasks.clear()
                            listData.checkedStates.clear()
                            listData.tasks.addAll(combined.map { it.first })
                            listData.checkedStates.addAll(combined.map { it.second })
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Unchecked", color = colorScheme.onSurface) },
                        onClick = {
                            sortMenuExpanded = false
                            val combined = listData.tasks.zip(listData.checkedStates)
                                .sortedBy { it.second }
                            listData.tasks.clear()
                            listData.checkedStates.clear()
                            listData.tasks.addAll(combined.map { it.first })
                            listData.checkedStates.addAll(combined.map { it.second })
                        }
                    )
                }
            }

            // Settings Dropdown
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
                    DropdownMenuItem(
                        text = { Text("Quicksave", color = colorScheme.onSurface) },
                        onClick = {
                            scope.launch {
                                dataStore.saveListData(listData.title.value, listData)
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("List saved as \"${listData.title.value}\"")
                            }
                            onMenuExpandChange(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("New List", color = colorScheme.onSurface) },
                        onClick = {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("New list!")
                            }
                            listData.reset()
                            onMenuExpandChange(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Save List", color = colorScheme.onSurface) },
                        onClick = {
                            onShowSaveDialog()
                            onMenuExpandChange(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Load List", color = colorScheme.onSurface) },
                        onClick = {
                            scope.launch {
                                updateSavedListNames(dataStore.getSavedListNames())
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