package com.youzbaki.nidham.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExportDialogBox(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    savedLists: List<Pair<String, String>>,
    onExportSelected: (List<String>) -> Unit,
    onExportAll: () -> Unit
) {
    val selectedIds = remember { mutableStateListOf<String>() }

    LaunchedEffect(showDialog) {
        if (!showDialog) selectedIds.clear()
    }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.background,
            onDismissRequest = onDismiss,
            confirmButton = {},
            title = { Text("Export Lists", color = colorScheme.onBackground) },
            text = {
                Column {
                    if (savedLists.isEmpty()) {
                        Text("No saved lists to export.", color = colorScheme.onSurface)
                    } else {
                        Text("Select lists to export:", color = colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            savedLists.sortedBy { it.second.lowercase() }.forEach { (id, title) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Checkbox(
                                        checked = id in selectedIds,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedIds.add(id)
                                            else selectedIds.remove(id)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            uncheckedColor = colorScheme.onBackground,
                                            checkedColor = colorScheme.primary,
                                            checkmarkColor = colorScheme.onPrimary
                                        )
                                    )
                                    Text(
                                        text = title,
                                        color = colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onExportSelected(selectedIds.toList())
                                onDismiss()
                            },
                            enabled = selectedIds.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.surface,
                                contentColor = colorScheme.onSurface,
                                disabledContainerColor = colorScheme.surface.copy(alpha = 0.4f),
                                disabledContentColor = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        ) {
                            Text("Export Selected (${selectedIds.size})")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                onExportAll()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.surface,
                                contentColor = colorScheme.onSurface
                            )
                        ) {
                            Text("Export All (${savedLists.size})")
                        }
                    }
                }
            }
        )
    }
}