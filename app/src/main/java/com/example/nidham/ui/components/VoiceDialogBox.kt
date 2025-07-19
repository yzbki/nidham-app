package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nidham.ListData
import com.example.nidham.OpenAIService
import com.example.nidham.ui.theme.GradientBrush
import kotlinx.coroutines.launch

@Composable
fun VoiceDialogBox(
    showDialog: Boolean,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    transcribedText: MutableState<String>,
    listData: ListData // <-- this is new
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GradientBrush),
            containerColor = colorScheme.background,
            onDismissRequest = {
                onDismiss()
                onStopRecording()
                transcribedText.value = ""
            },
            title = {
                Text("Voice Input", color = colorScheme.onBackground)
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
                        onValueChange = { transcribedText.value = it },
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

                    // Buttons row with Start/Stop and Generate
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                containerColor = if (isRecording) Color.Red else colorScheme.primary
                            )
                        ) {
                            Text(
                                if (isRecording) "Stop Recording" else "Start Recording",
                                color = colorScheme.onBackground
                            )
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null

                                    val result = OpenAIService.generateListDataFromPrompt(transcribedText.value)
                                    if (result != null) {
                                        listData.title.value = result.title.value
                                        listData.tasks.clear()
                                        listData.tasks.addAll(result.tasks)
                                        listData.checkedStates.clear()
                                        listData.checkedStates.addAll(result.checkedStates)
                                        onDismiss()
                                    } else {
                                        errorMessage = "Failed to generate response"
                                    }

                                    isLoading = false
                                }
                            },
                            enabled = transcribedText.value.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            )
                        ) {
                            Text(if (isLoading) "Generating..." else "Generate", color = colorScheme.onBackground)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel button on its own row, full width
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onDismiss()
                            onStopRecording()
                            transcribedText.value = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary
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