package com.example.nidham.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SaveDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    inputListName: String,
    onInputChange: (String) -> Unit,
    onSave: suspend (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = {
                onDismiss()
            },
            title = { Text("Save List", color = MaterialTheme.colorScheme.onBackground) },
            text = {
                TextField(
                    value = inputListName,
                    onValueChange = onInputChange,
                    label = { Text("List Name", color = MaterialTheme.colorScheme.onBackground) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        if (inputListName.isNotBlank()) {
                            onSave(inputListName)
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("List '$inputListName' saved!")
                        } else {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("Please enter a valid name.")
                        }
                    }
                    onDismiss()
                }) {
                    Text("Save", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismiss()
                }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}