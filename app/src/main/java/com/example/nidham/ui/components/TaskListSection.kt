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
import com.example.nidham.data.ListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.reorderable

@Composable
fun TaskListSection(
    listData: ListData,
    scope: CoroutineScope,
    dataStore: DataStoreManager,
    state: ReorderableLazyListState
) {
    // Title row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Delete button (multiple)
        IconButton(
            onClick = {
                val newItems = listData.items.filterIsInstance<ListItem.TaskItem>()
                    .withIndex()
                    .filterNot { listData.checkedStates.getOrElse(it.index) { false } }
                    .map { it.value }
                val newCheckedStates = listData.checkedStates.filter { !it }

                listData.items.clear()
                listData.items.addAll(newItems)
                listData.checkedStates.clear()
                listData.checkedStates.addAll(newCheckedStates)

                scope.launch { dataStore.saveListData(listData) }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = colorScheme.onBackground)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Selected Tasks")
        }

        // Title field
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
                cursorColor = colorScheme.onSurface
            )
        )
    }

    // Task list
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .reorderable(state)
            .background(colorScheme.background)
    ) {
        val taskItems = listData.items.filterIsInstance<ListItem.TaskItem>()

        itemsIndexed(taskItems, key = { _, item -> item.id }) { index, taskItem ->
            ReorderableItem(state, key = taskItem.id) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .detectReorder(state),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox
                    Checkbox(
                        checked = listData.checkedStates.getOrElse(index) { false },
                        onCheckedChange = {
                            listData.checkedStates[index] = it
                            scope.launch { dataStore.saveListData(listData) }
                        },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = colorScheme.onBackground,
                            checkedColor = colorScheme.background,
                            checkmarkColor = colorScheme.onSurface
                        )
                    )

                    // Task text field
                    TextField(
                        value = taskItem.textState.value,
                        onValueChange = { newValue ->
                            taskItem.textState.value = newValue
                            scope.launch { dataStore.saveListData(listData) }
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
                            cursorColor = colorScheme.onSurface,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* handle done */ })
                    )

                    // Delete button (individual)
                    IconButton(
                        onClick = {
                            listData.items.remove(taskItem)
                            if (listData.checkedStates.size > index) {
                                listData.checkedStates.removeAt(index)
                            }
                            scope.launch { dataStore.saveListData(listData) }
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}