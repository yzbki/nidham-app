package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
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
            .padding(end = 12.dp, bottom = 16.dp)
    ) {
        var selectAll by remember { mutableStateOf(false) }

        Checkbox(
            checked = selectAll,
            onCheckedChange = { isChecked ->
                selectAll = isChecked
                // Update all items' checked states
                listData.checkedStates.replaceAll { isChecked }
                // Persist changes
                scope.launch { dataStore.saveListData(listData) }
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = colorScheme.onBackground,
                checkedColor = colorScheme.primary,
                checkmarkColor = colorScheme.onPrimary
            )
        )

        // Title field
        TextField(
            value = listData.title.value,
            onValueChange = { newValue ->
                if (newValue.length <= ListData.MAX_TITLE_LENGTH) {
                    listData.title.value = newValue
                }
            },
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
    }

    // Task list
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .reorderable(state)
            .background(colorScheme.background)
            .drawVerticalScrollbar(state.listState)
    ) {
        val taskItems = listData.items.filterIsInstance<ListItem.TaskItem>()

        itemsIndexed(taskItems, key = { _, item -> item.id }) { index, taskItem ->
            ReorderableItem(state, key = taskItem.id) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp, bottom = 8.dp)
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
                            if (newValue.length <= ListData.MAX_TASK_LENGTH) {
                                taskItem.textState.value = newValue
                                scope.launch { dataStore.saveListData(listData) }
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
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            focusedIndicatorColor = colorScheme.background,
                            unfocusedIndicatorColor = colorScheme.background,
                            cursorColor = colorScheme.onSurface,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { })
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

// Scrollbar modifier
@Composable
fun Modifier.drawVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    trackColor: Color = colorScheme.surface,         // Full-length track
    thumbColor: Color = colorScheme.onSurface,       // Scroll indicator
    cornerRadius: Dp = 2.dp                          // Rounded edges for both
): Modifier = this.drawBehind {

    val totalItems = state.layoutInfo.totalItemsCount
    val visibleCount = state.layoutInfo.visibleItemsInfo.size
    if (totalItems == 0 || visibleCount == 0) return@drawBehind

    // Draw the full-height track
    drawRoundRect(
        color = trackColor,
        topLeft = Offset(size.width - width.toPx(), 0f),
        size = Size(width.toPx(), size.height),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )

    // Calculate the thumb size & position
    val proportion = visibleCount.toFloat() / totalItems
    val thumbHeight = size.height * proportion
    val scrollFraction = state.firstVisibleItemIndex.toFloat() /
            (totalItems - visibleCount).coerceAtLeast(1)
    val thumbOffsetY = (size.height - thumbHeight) * scrollFraction

    // Draw the scroll thumb
    drawRoundRect(
        color = thumbColor.copy(alpha = 0.2f),
        topLeft = Offset(size.width - width.toPx(), thumbOffsetY),
        size = Size(width.toPx(), thumbHeight),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
    )
}