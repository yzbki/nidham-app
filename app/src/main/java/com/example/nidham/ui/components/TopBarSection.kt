package com.example.nidham.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nidham.DataStoreManager
import com.example.nidham.ListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBarSection(
    colorScheme: ColorScheme,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    listData: ListData,
    dataStore: DataStoreManager,
    menuExpanded: Boolean,
    onMenuExpandChange: (Boolean) -> Unit,
    onShowSaveDialog: () -> Unit,
    onShowLoadDialog: suspend () -> Unit,
    updateSavedListNames: (List<String>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // App title text
        Text(
            text = "NIDHAM",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center
        )

        // Dropdown menu button box
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
                .weight(1f)
                .background(colorScheme.background),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(onClick = { onMenuExpandChange(true) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = colorScheme.onBackground
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { onMenuExpandChange(false) },
                modifier = Modifier.background(colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text("Reset List", color = colorScheme.onSurface) },
                    onClick = {
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar("List reset!")
                        }
                        listData.reset()
                        onMenuExpandChange(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Save List", color = colorScheme.onSurface) },
                    onClick = {
                        onShowSaveDialog()
                        onMenuExpandChange(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Load List", color = colorScheme.onSurface) },
                    onClick = {
                        scope.launch {
                            updateSavedListNames(dataStore.getSavedListNames())
                            onShowLoadDialog()
                        }
                        onMenuExpandChange(false)
                    }
                )
            }
        }
    }
}