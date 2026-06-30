package com.youzbaki.nidham.ui.components

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.data.ListItem
import com.youzbaki.nidham.service.AdManager
import com.youzbaki.nidham.service.OpenAIService
import com.youzbaki.nidham.service.SoundManager
import kotlinx.coroutines.launch

@Composable
fun AutoListDialogBox(
    showDialog: Boolean,
    showLabels: Boolean,
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
    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEnabled = transcribedText.value.isNotBlank() && !isLoading

    // Extracted generate logic, called from both the button and keyboard action
    val onGenerate = {
        SoundManager.playButton(context)
        scope.launch {
            isLoading = true
            errorMessage = null

            val result = OpenAIService.generateListDataFromPrompt(transcribedText.value)
            if (result != null) {
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
                errorMessage = "Failed to generate response, try again."
            }

            isLoading = false
        }
    }

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
                Column(
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                ) {
                    Text(
                        text = if (isRecording) "Listening..." else "Generate a list with a prompt!" +
                                "\nRecord for voice transcription.",
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
                        label = if (showLabels) {
                            { Text("Text Prompt") }
                        } else null,
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = true,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            if (isEnabled) onGenerate()
                            else focusManager.clearFocus()
                        }),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedLabelColor = colorScheme.onBackground,
                            unfocusedLabelColor = colorScheme.onBackground,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            focusedIndicatorColor = colorScheme.background,
                            unfocusedIndicatorColor = colorScheme.background,
                            cursorColor = colorScheme.onSurface
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Record Voice Button
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playButton(context)
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
                            Text(if (isRecording) "Stop" else "Record")
                        }

                        // Generate Auto-List button animation
                        val infiniteTransition = rememberInfiniteTransition(label = "gradientTransition")
                        val gradientOffset by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "gradientOffset"
                        )

                        val gradientColors = if (isLoading) listOf(
                            androidx.compose.ui.graphics.lerp(Color(0xFF7B2FBE), Color(0xFF4169E1), gradientOffset),
                            androidx.compose.ui.graphics.lerp(Color(0xFF4169E1), Color(0xFF9B30FF), gradientOffset),
                            androidx.compose.ui.graphics.lerp(Color(0xFF9B30FF), Color(0xFF7B2FBE), gradientOffset)
                        ) else listOf(colorScheme.surface, colorScheme.surface)

                        // Generate Auto-List button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isLoading) Brush.horizontalGradient(gradientColors)
                                    else Brush.horizontalGradient(
                                        listOf(
                                            colorScheme.surface.copy(alpha = if (isEnabled) 1f else 0.4f),
                                            colorScheme.surface.copy(alpha = if (isEnabled) 1f else 0.4f)
                                        )
                                    )
                                )
                                .then(
                                    if (isEnabled) Modifier.clickable { onGenerate() } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoading) "Generating" else "Generate",
                                color = if (isLoading) Color.White
                                else colorScheme.onSurface.copy(alpha = if (isEnabled) 1f else 0.4f),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cancel button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            SoundManager.playButton(context)
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