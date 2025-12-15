package com.youzbaki.nidham.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.data.ListItem
import com.youzbaki.nidham.service.AdManager
import com.youzbaki.nidham.service.OpenAIService
import kotlinx.coroutines.launch

@Composable
fun AutoListDialogBox(
    showDialog: Boolean,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNewList: (ListData) -> Unit,
    transcribedText: MutableState<String>,
    maxPromptLength: Int
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = {
                onDismiss()
                onStopRecording()
                transcribedText.value = ""
            },
            title = {
                Text("Auto-List", color = colorScheme.onBackground)
            },
            text = {
                Column {
                    Text(
                        text = if (isRecording) "Listening..." else "Press the button below to start recording.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = transcribedText.value,
                        onValueChange = { newValue ->
                            if (newValue.length <= maxPromptLength) {
                                transcribedText.value = newValue
                            }
                        },
                        label = { Text("Transcribed Text") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedLabelColor = colorScheme.onBackground,
                            unfocusedLabelColor = colorScheme.onBackground,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            focusedIndicatorColor = colorScheme.background,
                            unfocusedIndicatorColor = colorScheme.background,
                        ),
                    )
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Record Voice Button
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (isRecording) {
                                    onStopRecording()
                                } else {
                                    onStartRecording()
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) Color.Red else colorScheme.surface,
                                contentColor = colorScheme.onSurface
                            )
                        ) {
                            Text(if (isRecording) "Stop" else "Record Voice")
                        }

                        // Generate Auto-List Button
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null

                                    val result = OpenAIService.generateListDataFromPrompt(transcribedText.value)
                                    if (result != null) {
                                        // Show ad only AFTER successfully generating the list
                                        AdManager.showInterstitial(context as Activity) {
                                            val newList = ListData.newListData().apply {
                                                title.value = result.title.value.take(ListData.MAX_TITLE_LENGTH)
                                                items.clear()
                                                items.addAll(result.items.mapNotNull { li ->
                                                    if (li is ListItem.TaskItem) {
                                                        ListItem.TaskItem(
                                                            id = li.id,
                                                            textState = mutableStateOf(li.textState.value)
                                                        )
                                                    } else null
                                                })
                                                checkedStates.clear()
                                                val checks = result.checkedStates.take(items.filterIsInstance<ListItem.TaskItem>().size)
                                                checkedStates.addAll(checks)
                                                while (checkedStates.size < items.filterIsInstance<ListItem.TaskItem>().size) {
                                                    checkedStates.add(false)
                                                }
                                            }
                                            onNewList(newList)
                                            transcribedText.value = ""
                                            onDismiss()
                                        }
                                    } else {
                                        errorMessage = "Failed to generate response"
                                    }

                                    isLoading = false
                                }
                            },
                            enabled = transcribedText.value.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.surface,
                                contentColor = colorScheme.onSurface,
                                disabledContainerColor = colorScheme.surface.copy(alpha = 0.4f),
                                disabledContentColor = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(if (isLoading) "Generating..." else "Generate")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDismiss()
                            onStopRecording()
                            transcribedText.value = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface
                        )
                    ) {
                        Text("Cancel", color = colorScheme.onBackground)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}