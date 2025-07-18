package com.example.nidham.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nidham.VoiceRecognitionManager
import com.example.nidham.ui.theme.GradientBrush

@Composable
fun VoiceDialogBox(
    showDialog: Boolean,
    activity: Activity,
    voiceManager: VoiceRecognitionManager,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    transcribedText: MutableState<String>
) {
    if(showDialog) {
        AlertDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GradientBrush),
            containerColor = colorScheme.background,
            onDismissRequest = {
                onDismiss()
                onStopRecording()
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isRecording) {
                            onStopRecording()
                        } else {
                            onStartRecording()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red
                        else colorScheme.primary
                    )
                ) {
                    Text(if (isRecording) "Stop Recording" else "Start Recording",
                        color = colorScheme.onBackground)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        onStopRecording()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )) {
                    Text("Cancel", color = colorScheme.onBackground)
                }
            },
            title = { Text("Voice Input", color = colorScheme.onBackground) },
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

                }
            }
        )
    }
}