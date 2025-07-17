package com.example.nidham.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.example.nidham.ListData
import com.example.nidham.TaskItem

@Composable
fun BottomRowSection(
    listData: ListData,
    colorScheme: ColorScheme
) {
    Button(
        onClick = {
            listData.tasks.add(TaskItem())
            listData.checkedStates.add(false)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(WindowInsets.safeDrawing.asPaddingValues()),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        )
    ) {
        Text("Add Task")
    }
}