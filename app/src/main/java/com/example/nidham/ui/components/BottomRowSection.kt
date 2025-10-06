package com.example.nidham.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nidham.data.ListData
import com.example.nidham.data.TaskItem

@Composable
fun BottomRowSection(
    listData: ListData,
    colorScheme: ColorScheme,
    onVoiceInputClick: () -> Unit,
    isRecording: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Top full-width Auto-List button ---
        Button(
            onClick = onVoiceInputClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface.copy(alpha = 0.7f),
                contentColor = colorScheme.onSurface
            )
        ) {
            Text("Auto-List")
        }

        // --- Bottom row: Add Group + Add Task side by side ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // TODO: implement group creation
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface.copy(alpha = 0.7f),
                    contentColor = colorScheme.onSurface
                )
            ) {
                Text("Add Group")
            }

            Button(
                onClick = {
                    listData.tasks.add(TaskItem())
                    listData.checkedStates.add(false)
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