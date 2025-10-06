package com.example.nidham.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nidham.data.DataStoreManager
import com.example.nidham.data.ListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SaveDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    listData: ListData,
    inputListName: String,
    onInputChange: (String) -> Unit,
    dataStore: DataStoreManager,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = onDismiss,
            title = { Text("Save List", color = colorScheme.onBackground) },
            text = {
                TextField(
                    value = inputListName,
                    onValueChange = onInputChange,
                    label = { Text("List Name", color = colorScheme.onBackground) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            if (listData.isTitleValid(inputListName, dataStore)) {
                                listData.title.value = inputListName
                                dataStore.saveListData(listData)
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar("List '$inputListName' saved!")
                            } else {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    "ERROR: Invalid title."
                                )
                            }
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.onSurface
                    )
                ) {
                    Text("Save", color = colorScheme.onBackground)
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.onSurface
                    )
                ) {
                    Text("Cancel", color = colorScheme.onBackground)
                }
            }
        )
    }
}