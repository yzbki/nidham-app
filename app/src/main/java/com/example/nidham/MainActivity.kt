package com.example.nidham

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nidham.ui.components.BottomRowSection
import com.example.nidham.ui.components.LoadDialogBox
import com.example.nidham.ui.components.SaveDialogBox
import com.example.nidham.ui.components.VoiceDialogBox
import com.example.nidham.ui.components.TaskListSection
import com.example.nidham.ui.components.TopBarSection
import com.example.nidham.ui.theme.GradientBrush
import com.example.nidham.ui.theme.NidhamTheme
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NidhamTheme {
                ToDoListScreen()
            }
        }
    }
}

@Composable
fun ToDoListScreen() {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listData = remember { ListData() }
    var menuExpanded by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var inputListName by remember { mutableStateOf("") }
    var savedListNames by remember { mutableStateOf(listOf<String>()) }
    val focusManager = LocalFocusManager.current
    var isRecording by remember { mutableStateOf(false) }


    // Load autosave list
    LaunchedEffect(Unit) {
        val loadedData = dataStore.loadListData("AUTOSAVE")
        listData.title.value = loadedData.title.value
        listData.tasks.clear()
        listData.tasks.addAll(loadedData.tasks)
        listData.checkedStates.clear()
        listData.checkedStates.addAll(loadedData.checkedStates)
    }

    // Auto-save on any change
    LaunchedEffect(listData.title.value, listData.tasks.size, listData.checkedStates) {
        dataStore.saveListData("AUTOSAVE", listData)
    }

    // Keep checkedStates size in sync with tasks size
    LaunchedEffect(listData.tasks.size) {
        while (listData.checkedStates.size < listData.tasks.size)
            listData.checkedStates.add(false)
        while (listData.checkedStates.size > listData.tasks.size)
            listData.checkedStates.removeAt(listData.checkedStates.lastIndex)
    }

    // Set default list title when saving
    LaunchedEffect(showSaveDialog) {
        if (showSaveDialog) {
            inputListName = listData.title.value
        }
    }

    // Handle dynamic reordering of tasks
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            listData.tasks.apply {
                add(to.index, removeAt(from.index))
            }
            listData.checkedStates.apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

    // Voice manager
    val activity = LocalContext.current as Activity
    val voiceResult = remember { mutableStateOf("") }
    val voiceManager = remember { VoiceRecognitionManager(activity) }


    // User interface structure
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBrush)
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.onSurface,
                            actionColor = colorScheme.onBackground,
                            snackbarData = data
                        )
                    }
                )
            },
            containerColor = Color.Transparent,
            contentColor = colorScheme.onBackground,
            bottomBar = {
                BottomRowSection(
                    listData = listData,
                    colorScheme = colorScheme,
                    onVoiceInputClick = {
                        val permission = android.Manifest.permission.RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
                        } else {
                            showVoiceDialog = true
                        }
                    },
                    isRecording = isRecording
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                TopBarSection(
                    colorScheme = colorScheme,
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    listData = listData,
                    dataStore = dataStore,
                    menuExpanded = menuExpanded,
                    onMenuExpandChange = { menuExpanded = it },
                    onShowSaveDialog = { showSaveDialog = true },
                    onShowLoadDialog = { showLoadDialog = true },
                    updateSavedListNames = { savedListNames = it }
                )
                TaskListSection(
                    listData = listData,
                    scope = scope,
                    dataStore = dataStore,
                    state = state
                )
            }
            VoiceDialogBox(
                showDialog = showVoiceDialog,
                isRecording = isRecording,
                onDismiss = {
                    showVoiceDialog = false
                    isRecording = false
                },
                onStartRecording = {
                    isRecording = true
                    voiceManager.startListening(
                        onResult = { text ->
                            voiceResult.value = text
                            isRecording = false
                        },
                        onError = { errorMsg ->
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                            isRecording = false
                        }
                    )
                },
                onStopRecording = {
                    voiceManager.stopListening()
                    isRecording = false
                },
                transcribedText = voiceResult
            )
            SaveDialogBox(
                showDialog = showSaveDialog,
                onDismiss = {
                    showSaveDialog = false
                    inputListName = ""
                },
                inputListName = inputListName,
                onInputChange = { inputListName = it },
                onSave = { listName ->
                    dataStore.saveListData(listName, listData)
                },
                snackbarHostState = snackbarHostState,
                scope = scope
            )
            LoadDialogBox(
                showDialog = showLoadDialog,
                onDismiss = { showLoadDialog = false },
                savedListNames = savedListNames,
                onLoad = { name ->
                    val loaded = dataStore.loadListData(name)
                    listData.title.value = loaded.title.value
                    listData.tasks.clear()
                    listData.tasks.addAll(loaded.tasks)
                    listData.checkedStates.clear()
                    listData.checkedStates.addAll(loaded.checkedStates)
                    while (listData.checkedStates.size < listData.tasks.size)
                        listData.checkedStates.add(false)
                },
                onDelete = { name ->
                    dataStore.deleteListByName(name)
                    savedListNames = savedListNames - name
                },
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
    }
}