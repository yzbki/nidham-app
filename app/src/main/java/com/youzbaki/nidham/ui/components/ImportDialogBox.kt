package com.youzbaki.nidham.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.service.SoundManager

@Composable
fun ImportDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onPickFile: () -> Unit
) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = {
                SoundManager.playButton(context)
                onDismiss() },
            confirmButton = { SoundManager.playButton(context) },
            title = { Text("Import Lists", color = colorScheme.onBackground) },
            text = {
                Column {
                    Text(
                        text = "Select a Nidham JSON file to import lists from. ",
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            SoundManager.playButton(context)
                            onPickFile()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface
                        )
                    ) {
                        Text("Choose File")
                    }
                }
            }
        )
    }
}