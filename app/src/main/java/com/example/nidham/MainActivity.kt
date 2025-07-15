package com.example.nidham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.nidham.ui.theme.NidhamTheme
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NidhamTheme {
                ToDoListScreen()
            }
        }
    }
}

@Composable
fun ToDoListScreen() {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listData = remember { ListData() }
    val autoSaveEnabled = remember { mutableStateOf(true) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Auto-load on launch
    LaunchedEffect(Unit) {
        val loadedData = dataStore.loadListData()
        listData.title.value = loadedData.title.value
        listData.tasks.clear()
        listData.tasks.addAll(loadedData.tasks)
        listData.checkedStates.clear()
        listData.checkedStates.addAll(loadedData.checkedStates)
    }

    // Auto-save on any change
    LaunchedEffect(listData.title.value, listData.tasks.size, listData.checkedStates) {
        if (autoSaveEnabled.value) {
            dataStore.saveListData(listData)
        }
    }

    // Keep checkedStates size in sync with tasks size
    LaunchedEffect(listData.tasks.size) {
        while (listData.checkedStates.size < listData.tasks.size)
            listData.checkedStates.add(false)
        while (listData.checkedStates.size > listData.tasks.size)
            listData.checkedStates.removeAt(listData.checkedStates.lastIndex)
    }

    // Handle dynamic reordering of tasks
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            listData.tasks.apply {
                add(to.index, removeAt(from.index))
            }
            listData.checkedStates.apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colorScheme.background,
        contentColor = colorScheme.onBackground,
        bottomBar = {
            // Add task button
            Button(
                onClick = {
                    listData.tasks.add(TaskItem())
                    listData.checkedStates.add(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(WindowInsets.safeDrawing.asPaddingValues()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.secondary
                )
            ) {
                Text("Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Taskbar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                // App title text
                Text(
                    text = "Nidham",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )
                // Dropdown menu box
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopEnd)
                        .weight(1f)
                        .background(colorScheme.primary),
                    contentAlignment = Alignment.TopEnd
                ) {
                    // Menu button
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = colorScheme.secondary
                        )
                    }
                    // Dropdown menu items
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(colorScheme.tertiary)
                    ) {
                        // Reset List
                        DropdownMenuItem(
                            text = {
                                Text("Reset List", color = colorScheme.onSurface)
                            },
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("List reset!")
                                }
                                listData.reset()
                                menuExpanded = false
                            }
                        )
                        // Save List
                        DropdownMenuItem(
                            text = {
                                Text("Save List", color = colorScheme.onSurface)
                            },
                            onClick = {
                                scope.launch {
                                    dataStore.saveListData(listData)
                                    snackbarHostState.showSnackbar("List saved!")
                                }
                                menuExpanded = false
                            }
                        )
                        // Load List
                        DropdownMenuItem(
                            text = {
                                Text("Load List", color = colorScheme.onSurface)
                            },
                            onClick = {
                                scope.launch {
                                    autoSaveEnabled.value = false
                                    val loadedData = dataStore.loadListData()
                                    listData.title.value = loadedData.title.value
                                    listData.tasks.clear()
                                    listData.tasks.addAll(loadedData.tasks)
                                    listData.checkedStates.clear()
                                    listData.checkedStates.addAll(loadedData.checkedStates)

                                    // Pad checkedStates if mismatched
                                    while (listData.checkedStates.size < listData.tasks.size)
                                        listData.checkedStates.add(false)

                                    snackbarHostState.showSnackbar("List loaded!")
                                }
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
            // List title text
            TextField(
                value = listData.title.value,
                onValueChange = { listData.title.value = it },
                label = { Text("List Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = colorScheme.onSurface,
                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 1.0f),
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
                    focusedIndicatorColor = colorScheme.primary,
                    unfocusedIndicatorColor = colorScheme.primary
                )
            )
            // Task list
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .weight(1f)
                    .reorderable(state)
                    .detectReorderAfterLongPress(state)
            ) {
                itemsIndexed(listData.tasks, key = { _, taskItem -> taskItem.id }) { index, taskItem ->
                    ReorderableItem(state, key = taskItem.id) { _ ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox button
                            Checkbox(
                                checked = listData.checkedStates.getOrElse(index) { false },
                                onCheckedChange = { listData.checkedStates[index] = it }
                            )
                            // Task text field
                            TextField(
                                value = taskItem.textState.value,
                                onValueChange = { newValue ->
                                    taskItem.textState.value = newValue
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Task ${index + 1}") },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (listData.checkedStates.getOrElse(index) { false })
                                        colorScheme.onSurface.copy(alpha = 0.4f)
                                    else
                                        colorScheme.onSurface,
                                    textDecoration = if (listData.checkedStates.getOrElse(index) { false })
                                        TextDecoration.LineThrough
                                    else
                                        null
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedLabelColor = colorScheme.onSurface,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 1.0f),
                                    focusedContainerColor = colorScheme.surface,
                                    unfocusedContainerColor = colorScheme.surface,
                                    focusedTextColor = colorScheme.onSurface,
                                    unfocusedTextColor = colorScheme.onSurface,
                                    focusedIndicatorColor = colorScheme.primary,
                                    unfocusedIndicatorColor = colorScheme.primary
                                )
                            )
                            // Remove task button
                            IconButton(
                                onClick = {
                                    listData.tasks.removeAt(index)
                                    listData.checkedStates.removeAt(index)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Task"
                                )
                            }
                            // Drag task button
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Drag Handle",
                                tint = colorScheme.secondary,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}