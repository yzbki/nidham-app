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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
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
    var menuExpanded by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var inputListName by remember { mutableStateOf("") }
    var savedListNames by remember { mutableStateOf(listOf<String>()) }

    // Load autosave list
    LaunchedEffect(Unit) {
        val loadedData = dataStore.loadListData("AUTOSAVE")
        listData.title.value = loadedData.title.value
        listData.tasks.clear()
        listData.tasks.addAll(loadedData.tasks)
        listData.checkedStates.clear()
        listData.checkedStates.addAll(loadedData.checkedStates)
    }

    // Auto-save on any change
    LaunchedEffect(listData.title.value, listData.tasks.size, listData.checkedStates) {
        dataStore.saveListData("AUTOSAVE", listData)
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
                // Dropdown menu button box
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopEnd)
                        .weight(1f)
                        .background(colorScheme.background),
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
                        // Reset list button
                        DropdownMenuItem(
                            text = { Text("Reset List", color = colorScheme.onSurface) },
                            onClick = {
                                scope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("List reset!")
                                }
                                listData.reset()
                                menuExpanded = false
                            }
                        )
                        // Save list button
                        DropdownMenuItem(
                            text = { Text("Save List", color = colorScheme.onSurface) },
                            onClick = {
                                showSaveDialog = true
                                menuExpanded = false
                            }
                        )
                        // Load list button
                        DropdownMenuItem(
                            text = { Text("Load List", color = colorScheme.onSurface) },
                            onClick = {
                                scope.launch {
                                    savedListNames = dataStore.getSavedListNames()
                                    showLoadDialog = true
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
                    unfocusedLabelColor = colorScheme.onSurface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.background,
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
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
                                onCheckedChange = {
                                    listData.checkedStates[index] = it
                                    scope.launch {
                                        dataStore.saveListData("AUTOSAVE", listData)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    uncheckedColor = colorScheme.surface,
                                    checkedColor = colorScheme.onSurface
                                )
                            )
                            // Task text field
                            TextField(
                                value = taskItem.textState.value,
                                onValueChange = { newValue ->
                                    taskItem.textState.value = newValue
                                    scope.launch {
                                        dataStore.saveListData("AUTOSAVE", listData)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Task ${index + 1}") },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (listData.checkedStates.getOrElse(index) { false })
                                        colorScheme.inverseSurface.copy(alpha = 0.4f)
                                    else
                                        colorScheme.inverseSurface,
                                    textDecoration = if (listData.checkedStates.getOrElse(index) { false })
                                        TextDecoration.LineThrough
                                    else
                                        null
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (listData.checkedStates.getOrElse(index) { false })
                                        colorScheme.surface.copy(alpha = 0.8f)
                                    else
                                        colorScheme.surface,
                                    unfocusedContainerColor = if (listData.checkedStates.getOrElse(index) { false })
                                        colorScheme.surface.copy(alpha = 0.8f)
                                    else
                                        colorScheme.surface,
                                    focusedLabelColor = colorScheme.onSurface,
                                    unfocusedLabelColor = colorScheme.onSurface,
                                    focusedTextColor = colorScheme.onSurface,
                                    unfocusedTextColor = colorScheme.onSurface,
                                    focusedIndicatorColor = colorScheme.primary,
                                    unfocusedIndicatorColor = colorScheme.primary,
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
        // Save dialog box
        if (showSaveDialog) {
            inputListName = listData.title.value
            AlertDialog(
                onDismissRequest = {
                    showSaveDialog = false
                    inputListName = ""
                },
                title = { Text("Save List",
                    color = colorScheme.secondary)
                },
                text = {
                    TextField(
                        value = inputListName,
                        onValueChange = { inputListName = it },
                        label = { Text("List Name",
                            color = colorScheme.secondary)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                if (inputListName.isNotBlank()) {
                                    dataStore.saveListData(inputListName, listData)
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("List '$inputListName' saved!")
                                } else {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar("Please enter a valid name.")
                                }
                            }
                            showSaveDialog = false
                        }
                    ) { Text("Save") }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showSaveDialog = false
                            inputListName = ""
                        }
                    ) { Text("Cancel") }
                }
            )

        }
        // Load dialog box
        if (showLoadDialog) {
            AlertDialog(
                onDismissRequest = { showLoadDialog = false },
                confirmButton = {},
                title = {
                    Text(
                        "Load List",
                        color = colorScheme.secondary
                    )
                },
                text = {
                    Column {
                        if (savedListNames.isEmpty()) {
                            Text("No saved lists found.")
                        } else {
                            savedListNames.forEach() { name ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                val loaded = dataStore.loadListData(name)
                                                listData.title.value = loaded.title.value
                                                listData.tasks.clear()
                                                listData.tasks.addAll(loaded.tasks)
                                                listData.checkedStates.clear()
                                                listData.checkedStates.addAll(loaded.checkedStates)
                                                while (listData.checkedStates.size < listData.tasks.size)
                                                    listData.checkedStates.add(false)
                                                snackbarHostState.currentSnackbarData?.dismiss()
                                                snackbarHostState.showSnackbar("Loaded \"$name\"")
                                            }
                                            showLoadDialog = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text(
                                            name,
                                            color = colorScheme.secondary
                                        )
                                    }
                                    if (name != "AUTOSAVE") {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    dataStore.deleteListByName(name)
                                                    savedListNames = savedListNames - name
                                                    snackbarHostState.currentSnackbarData?.dismiss()
                                                    snackbarHostState.showSnackbar("Deleted \"$name\"")
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete $name"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}