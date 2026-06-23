package com.youzbaki.nidham.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.DataStoreManager
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.data.ListItem
import com.youzbaki.nidham.service.SoundManager
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
    state: ReorderableLazyListState,
    sortMode: String,
    pushUndo: () -> Unit,
    showLabels: Boolean,
    textFieldSquared: Boolean
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val focusManager = LocalFocusManager.current

    // Title row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // Select-all checkbox
        Checkbox(
            checked = listData.selectAll.value,
            onCheckedChange = { isChecked ->
                SoundManager.playCheck(context)
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
            shape = if (textFieldSquared) TextFieldDefaults.shape else RoundedCornerShape(32.dp),
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
    ) {
        itemsIndexed(displayItems, key = { _, pair -> pair.second.id }) { _, (originalIndex, taskItem) ->
            ReorderableItem(state, key = taskItem.id) { isDragging ->
                val checkScale = remember { androidx.compose.animation.core.Animatable(1f) }
                val context = androidx.compose.ui.platform.LocalContext.current
                val viewConfig = LocalViewConfiguration.current
                val focusRequester = remember { FocusRequester() }
                var isFocused by remember { mutableStateOf(false) }

                // Drag animation scale
                val dragScale by animateFloatAsState(
                    targetValue = if (isDragging) 1.07f else 1f,
                    animationSpec = tween(durationMillis = 150),
                    label = "dragScale"
                )

                CompositionLocalProvider(
                    LocalViewConfiguration provides object : ViewConfiguration by viewConfig {
                        override val longPressTimeoutMillis get() = 200L
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            // Long-press to drag item; swipe to scroll
                            .then(
                                if (sortMode == "custom")
                                    Modifier.detectReorderAfterLongPress(state)
                                else
                                    Modifier
                            )
                            // Lift animation
                            .graphicsLayer {
                                scaleX = dragScale
                                scaleY = dragScale
                            },
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
                                    checkScale.animateTo(0.95f, animationSpec = tween(60))
                                    checkScale.animateTo(1f, animationSpec = tween(60))
                                }
                                SoundManager.playCheck(context)
                            },
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = colorScheme.onBackground,
                                checkedColor = colorScheme.background,
                                checkmarkColor = colorScheme.onSurface
                            )
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer { scaleX = checkScale.value; scaleY = checkScale.value }
                        ) {
                            TextField(
                                value = taskItem.textState.value,
                                onValueChange = { newValue ->
                                    if (newValue.length <= ListData.MAX_TASK_LENGTH) {
                                        taskItem.textState.value = newValue
                                        scope.launch { dataStore.saveListData(listData) }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { state ->
                                        isFocused = state.isFocused
                                        pushUndo()
                                    },
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
                                shape = if (textFieldSquared) TextFieldDefaults.shape else RoundedCornerShape(32.dp),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrect = true,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                })
                            )

                            // Textfield tap detection
                            if (!isFocused && sortMode == "custom") {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .detectReorderAfterLongPress(state)
                                        .pointerInput(Unit) {
                                            detectTapGestures(onTap = { focusRequester.requestFocus() })
                                        }
                                )
                            }
                        }

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
}