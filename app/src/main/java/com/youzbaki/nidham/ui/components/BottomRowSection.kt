package com.youzbaki.nidham.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.data.ListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomRowSection(
    listData: ListData,
    colorScheme: ColorScheme,
    onVoiceInputClick: () -> Unit,
    isRecording: Boolean,
    snackbarHostState: SnackbarHostState,
    pushUndo: () -> Unit,
    scope: CoroutineScope
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top row
        /* TODO: implement group creation
        Button(
            onClick = {
                // Create group
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface.copy(alpha = 0.7f),
                contentColor = colorScheme.onSurface
            )
        ) {
            Text("Add Group")
        }
         */

        // Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onVoiceInputClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface.copy(alpha = 0.7f),
                    contentColor = colorScheme.onSurface
                )
            ) {
                Text("Auto-List")
            }

            Button(
                onClick = {
                    pushUndo()
                    val added = listData.addTask()
                    if (!added) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "You reached the maximum of ${ListData.MAX_TASKS} tasks.",
                                withDismissAction = true
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface.copy(alpha = 0.7f),
                    contentColor = colorScheme.onSurface
                )
            ) {
                Text("Add Task")
            }

        }
    }
}