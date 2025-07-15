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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.nidham.ui.theme.NidhamTheme
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.util.UUID

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

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val textState: MutableState<String> = mutableStateOf("")
)

class ListData {
    val title = mutableStateOf("To-Do List")
    val tasks = mutableStateListOf(TaskItem())
    val checkedStates = mutableStateListOf(false)

    fun reset() {
        title.value = "To-Do List"
        tasks.clear()
        checkedStates.clear()
        tasks.add(TaskItem())
        checkedStates.add(false)
    }
}

@Composable
fun ToDoListScreen() {
    val listData = remember { ListData() }
    var menuExpanded by remember { mutableStateOf(false) }

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
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
                        // Reset list
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Reset List",
                                    color = colorScheme.onSurface
                                )
                            },
                            onClick = {
                                listData.reset()
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
            // Add task button
            Button(
                onClick = {
                    listData.tasks.add(TaskItem())
                    listData.checkedStates.add(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.secondary
                )
            ) { Text("Add Task") }
        }
    }
}