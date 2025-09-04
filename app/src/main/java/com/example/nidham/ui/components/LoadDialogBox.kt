package com.example.nidham.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoadDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    savedListNames: List<String>,
    onLoad: suspend (String) -> Unit,
    onDelete: suspend (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = onDismiss,
            confirmButton = {},
            title = {
                Text("Load List", color = colorScheme.onBackground)
            },
            text = {
                Column {
                    if (savedListNames.isEmpty()) {
                        Text("No saved lists found.")
                    } else {
                        savedListNames.sortedBy { it.lowercase() }.forEach { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            onLoad(name)
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            snackbarHostState.showSnackbar("Loaded \"$name\"")
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
                                    Text(name, color = colorScheme.onSurface)
                                }
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            onDelete(name)
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            snackbarHostState.showSnackbar("Deleted \"$name\"")
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = colorScheme.onBackground
                                    )
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
        )
    }
}