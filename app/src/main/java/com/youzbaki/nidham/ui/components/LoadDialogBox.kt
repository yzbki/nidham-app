package com.youzbaki.nidham.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.service.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun LoadDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    savedLists: List<Pair<String, String>>,
    onLoad: suspend (String) -> Unit,
    onDelete: suspend (String) -> Unit,
    onReorder: (List<String>) -> Unit, // NEW
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    var listToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    val localItems = remember { mutableStateListOf<Pair<String, String>>() }

    LaunchedEffect(savedLists) {
        localItems.clear()
        localItems.addAll(savedLists)
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            localItems.add(to.index, localItems.removeAt(from.index))
        },
        onDragEnd = { _, _ ->
            onReorder(localItems.map { it.first })
        }
    )

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = {
                SoundManager.playButton(context)
                onDismiss()
            },
            confirmButton = { SoundManager.playButton(context) },
            title = { Text("Load List", color = colorScheme.onBackground) },
            text = {
                Column {
                    if (localItems.isEmpty()) {
                        Text("No saved lists found.", color = colorScheme.onSurface)
                    } else {
                        LazyColumn(
                            state = reorderState.listState,
                            modifier = Modifier
                                .heightIn(max = 320.dp)
                                .reorderable(reorderState)
                        ) {
                            itemsIndexed(localItems, key = { _, item -> item.first }) { _, (id, title) ->
                                ReorderableItem(reorderState, key = id) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .detectReorder(reorderState),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                SoundManager.playButton(context)
                                                scope.launch {
                                                    onLoad(id)
                                                    snackbarHostState.currentSnackbarData?.dismiss()
                                                    snackbarHostState.showSnackbar("Loaded \"$title\"")
                                                }
                                                onDismiss()
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorScheme.surface,
                                                contentColor = colorScheme.onSurface
                                            )
                                        ) {
                                            Text(title, color = colorScheme.onSurface)
                                        }
                                        IconButton(
                                            onClick = {
                                                SoundManager.playButton(context)
                                                listToDelete = id to title },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                contentColor = colorScheme.onBackground
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete $title"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // Confirmation dialog for deletion
    listToDelete?.let { (id, title) ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = { Text("Confirm Delete", color = colorScheme.onBackground) },
            text = { Text("Are you sure you want to delete \"$title\"?", color = colorScheme.onSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        SoundManager.playButton(context)
                        scope.launch {
                            onDelete(id)
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Deleted \"$title\"")
                        }
                        listToDelete = null
                    }
                ) {
                    Text("Delete", color = colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    SoundManager.playButton(context)
                    listToDelete = null }) {
                    Text("Cancel", color = colorScheme.onBackground)
                }
            },
            containerColor = colorScheme.background
        )
    }
}