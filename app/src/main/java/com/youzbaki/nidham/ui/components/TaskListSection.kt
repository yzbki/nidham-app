package com.youzbaki.nidham.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.DataStoreManager
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.data.ListItem
import com.youzbaki.nidham.service.SoundManager
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
    state: ReorderableLazyListState,
    sortMode: String,
    pushUndo: () -> Unit,
    showLabels: Boolean,
    textFieldSquared: Boolean
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Title row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, end = 12.dp)
    ) {
        Checkbox(
            checked = listData.selectAll.value,
            onCheckedChange = { isChecked ->
                SoundManager.playButton(context)
                pushUndo()
                listData.selectAll.value = isChecked
                listData.checkedStates.replaceAll { isChecked }
                scope.launch {
                    dataStore.saveListData(listData)
                }
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
            label = if (showLabels) {
                { Text("List Title") }
            } else null,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { _ -> pushUndo() },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = colorScheme.onBackground,
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
                SoundManager.playDelete(context)
                pushUndo()
                val newItems = listData.items.filterIsInstance<ListItem.TaskItem>()
                    .withIndex()
                    .filterNot { listData.checkedStates.getOrElse(it.index) { false } }
                    .map { it.value }
                val newCheckedStates = listData.checkedStates.filter { !it }

                listData.items.clear()
                listData.items.addAll(newItems)
                listData.checkedStates.clear()
                listData.checkedStates.addAll(newCheckedStates)
                listData.selectAll.value = listData.checkedStates.isNotEmpty() &&
                        listData.checkedStates.all { it }

                scope.launch { dataStore.saveListData(listData) }
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = colorScheme.onBackground)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Selected Tasks")
        }
    }

    val taskItems = listData.items.filterIsInstance<ListItem.TaskItem>()

    // Produce a list of items sorted by mode
    val displayItems: List<Pair<Int, ListItem.TaskItem>> = run {
        val indexed = taskItems.mapIndexed { i, item -> i to item }
        when (sortMode) {
            "checked"   -> indexed.sortedByDescending { listData.checkedStates.getOrElse(it.first) { false } }
            "unchecked" -> indexed.sortedBy           { listData.checkedStates.getOrElse(it.first) { false } }
            else        -> indexed
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
        itemsIndexed(displayItems, key = { _, pair -> pair.second.id }) { _, (originalIndex, taskItem) ->
        ReorderableItem(state, key = taskItem.id) {
                val scale = remember { androidx.compose.animation.core.Animatable(1f) }
                val context = androidx.compose.ui.platform.LocalContext.current

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 12.dp)
                        .then(if (sortMode == "custom") Modifier.detectReorder(state) else Modifier),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox
                    Checkbox(
                        checked = listData.checkedStates.getOrElse(originalIndex) { false },
                        onCheckedChange = { checked ->
                            pushUndo()
                            listData.checkedStates[originalIndex] = checked
                            if (!checked) listData.selectAll.value = false
                            else if (listData.allChecked()) listData.selectAll.value = true
                            scope.launch {
                                dataStore.saveListData(listData)
                                scale.animateTo(0.95f, animationSpec = tween(60))
                                scale.animateTo(1f, animationSpec = tween(60))
                            }
                            SoundManager.playCheck(context)
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
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                            .onFocusChanged { _ -> pushUndo() },
                        label = if (showLabels) {
                            { Text("Task ${originalIndex + 1}") }
                        } else null,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = if (listData.checkedStates.getOrElse(originalIndex) { false })
                                colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                colorScheme.onSurface,
                            textDecoration = if (listData.checkedStates.getOrElse(originalIndex) { false })
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
                        shape = if(textFieldSquared) { TextFieldDefaults.shape }
                        else {RoundedCornerShape(32.dp)},
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { })
                    )

                    // Delete button (individual)
                    IconButton(
                        onClick = {
                            SoundManager.playDelete(context)
                            pushUndo()
                            listData.items.remove(taskItem)
                            if (listData.checkedStates.size > originalIndex) {
                                listData.checkedStates.removeAt(originalIndex)
                            }
                            if (listData.allChecked()) listData.selectAll.value = true
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