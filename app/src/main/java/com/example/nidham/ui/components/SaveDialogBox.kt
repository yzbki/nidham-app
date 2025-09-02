package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.nidham.ui.theme.GradientBrush
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
            /*
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GradientBrush),
             */
            containerColor = colorScheme.background,
            onDismissRequest = {
                onDismiss()
            },
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
                        unfocusedContainerColor = colorScheme.surface,
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
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurface
                )) {
                    Text("Save", color = colorScheme.onBackground)
                }
            },
            dismissButton = {
                Button(onClick = {
                    onDismiss()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurface
                )) {
                    Text("Cancel", color = colorScheme.onBackground)
                }
            }
        )
    }
}