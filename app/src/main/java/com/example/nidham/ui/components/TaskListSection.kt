package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.nidham.data.DataStoreManager
import com.example.nidham.data.ListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.reorderable

@Composable
fun TaskListSection(
    listData: ListData,
    scope: CoroutineScope,
    dataStore: DataStoreManager,
    state: ReorderableLazyListState
) {
    // Title row with select all checkbox
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // "Multiple Delete" Icon Button
        IconButton(
            onClick = {
                val newTasks = listData.tasks
                    .withIndex()
                    .filterNot { listData.checkedStates.getOrElse(it.index) { false } }
                    .map { it.value }
                val newCheckedStates = listData.checkedStates.filter { !it }
                listData.tasks.clear()
                listData.tasks.addAll(newTasks)
                listData.checkedStates.clear()
                listData.checkedStates.addAll(newCheckedStates)
                scope.launch {
                    dataStore.saveListData(listData)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Selected Tasks"
            )
        }

        // Title TextField
        TextField(
            value = listData.title.value,
            onValueChange = { listData.title.value = it },
            label = { Text("List Title") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedLabelColor = colorScheme.onBackground,
                unfocusedLabelColor = colorScheme.onBackground,
                focusedContainerColor = colorScheme.surface.copy(alpha = 0.7f),
                unfocusedContainerColor = colorScheme.background,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onBackground,
            )
        )
    }

    // Task list rendering
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .reorderable(state)
            .background(colorScheme.background)
    ) {
        itemsIndexed(listData.tasks, key = { _, taskItem -> taskItem.id }) { index, taskItem ->
            ReorderableItem(state, key = taskItem.id) { _ ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .detectReorderAfterLongPress(state),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkboxes
                    Checkbox(
                        checked = listData.checkedStates.getOrElse(index) { false },
                        onCheckedChange = {
                            listData.checkedStates[index] = it
                            scope.launch {
                                dataStore.saveListData(listData)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = colorScheme.onBackground,
                            checkedColor = colorScheme.background,
                            checkmarkColor = colorScheme.onSurface
                        )
                    )

                    // Task list
                    TextField(
                        value = taskItem.textState.value,
                        onValueChange = { newValue ->
                            taskItem.textState.value = newValue
                            scope.launch {
                                dataStore.saveListData(listData)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Task ${index + 1}") },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = if (listData.checkedStates.getOrElse(index) { false })
                                colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                colorScheme.onSurface.copy(alpha = 0.7f),
                            textDecoration = if (listData.checkedStates.getOrElse(index) { false })
                                TextDecoration.LineThrough else null
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedLabelColor = colorScheme.onBackground.copy(alpha = 0.7f),
                            unfocusedLabelColor = colorScheme.onBackground.copy(alpha = 0.7f),
                            focusedTextColor = colorScheme.onSurface.copy(alpha = 0.7f),
                            unfocusedTextColor = colorScheme.onSurface.copy(alpha = 0.7f),
                            focusedIndicatorColor = colorScheme.background,
                            unfocusedIndicatorColor = colorScheme.background,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* handle done */ })
                    )

                    // Drag handle
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