package com.example.nidham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.nidham.ui.theme.NidhamTheme
import org.burnoutcrew.reorderable.*
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

@Composable
fun ToDoListScreen() {
    var listTitle by remember { mutableStateOf("My To-Do List") }

    val tasks = remember {
        mutableStateListOf(
            TaskItem(textState = mutableStateOf("First task")),
            TaskItem(textState = mutableStateOf("Second task"))
        )
    }
    val checkedStates = remember { mutableStateListOf(false, false) }

    // Keep checkedStates size in sync with tasks size
    LaunchedEffect(tasks.size) {
        while (checkedStates.size < tasks.size) checkedStates.add(false)
        while (checkedStates.size > tasks.size) checkedStates.removeAt(checkedStates.lastIndex)
    }

    // Handle dynamic reordering of tasks
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            tasks.apply {
                add(to.index, removeAt(from.index))
            }
            checkedStates.apply {
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
            // App title text
            Text(
                text = "Nidham",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            // List title text
            TextField(
                value = listTitle,
                onValueChange = { listTitle = it },
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
                itemsIndexed(tasks, key = { _, taskItem -> taskItem.id }) { index, taskItem ->
                    ReorderableItem(state, key = taskItem.id) { _ ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox button
                            Checkbox(
                                checked = checkedStates.getOrElse(index) { false },
                                onCheckedChange = { checkedStates[index] = it }
                            )
                            TextField(
                                value = taskItem.textState.value,
                                onValueChange = { newValue ->
                                    taskItem.textState.value = newValue
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Task ${index + 1}") },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (checkedStates.getOrElse(index) { false })
                                        colorScheme.onSurface.copy(alpha = 0.4f)
                                    else
                                        colorScheme.onSurface,
                                    textDecoration = if (checkedStates.getOrElse(index) { false })
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
                            IconButton(onClick = {
                                tasks.removeAt(index)
                                checkedStates.removeAt(index)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Task"
                                )
                            }
                            // Drag task button
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Drag Handle",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            // Add task button
            Button(
                onClick = {
                    tasks.add(TaskItem(textState = mutableStateOf("")))
                    checkedStates.add(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Add Task")
            }
        }
    }
}