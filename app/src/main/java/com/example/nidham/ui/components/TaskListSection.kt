package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.nidham.DataStoreManager
import com.example.nidham.ListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.*

@Composable
fun TaskListSection(
    listData: ListData,
    scope: CoroutineScope,
    dataStore: DataStoreManager,
    state: ReorderableLazyListState
) {
    // List title input
    TextField(
        value = listData.title.value,
        onValueChange = { listData.title.value = it },
        label = { Text("List Title") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedLabelColor = colorScheme.onBackground,
            unfocusedLabelColor = colorScheme.onBackground,
            focusedContainerColor = colorScheme.surface.copy(alpha = 0.8f),
            unfocusedContainerColor = colorScheme.background,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onBackground,
        )
    )

    // Task list with reordering
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .reorderable(state)
            .background(colorScheme.background)
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
                            checkedColor = colorScheme.onBackground
                        )
                    )

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
                                colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                colorScheme.onSurface,
                            textDecoration = if (listData.checkedStates.getOrElse(index) { false })
                                TextDecoration.LineThrough else null
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface.copy(alpha = 0.8f),
                            unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.8f),
                            focusedLabelColor = colorScheme.onBackground,
                            unfocusedLabelColor = colorScheme.onBackground,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            focusedIndicatorColor = colorScheme.background,
                            unfocusedIndicatorColor = colorScheme.background,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* handle done */ })
                    )

                    IconButton(
                        onClick = {
                            listData.tasks.removeAt(index)
                            listData.checkedStates.removeAt(index)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task"
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drag Handle",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}