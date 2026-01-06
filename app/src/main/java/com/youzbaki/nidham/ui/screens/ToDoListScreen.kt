package com.youzbaki.nidham.ui.screens

import android.app.Activity
import android.content.pm.PackageManager
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
import com.youzbaki.nidham.data.DataStoreManager
import com.youzbaki.nidham.data.ListData
import com.youzbaki.nidham.data.ListItem
import com.youzbaki.nidham.service.VoiceRecognitionManager
import com.youzbaki.nidham.ui.components.BottomRowSection
import com.youzbaki.nidham.ui.components.LoadDialogBox
import com.youzbaki.nidham.ui.components.SaveDialogBox
import com.youzbaki.nidham.ui.components.TaskListSection
import com.youzbaki.nidham.ui.components.TopBarSection
import com.youzbaki.nidham.ui.components.AutoListDialogBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

@Composable
fun ToDoListScreen(
    themeMode: String,
    colorVariant: String,
    onThemeChange: (themeMode: String, colorVariant: String) -> Unit
) {
    val MAX_PROMPT_LENGTH = 200
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }
    var inputListName by remember { mutableStateOf("") }
    var currentListData by remember { mutableStateOf(ListData()) }
    val undoStack = remember { ArrayDeque<ListData>() }
    var savedLists by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val snackbarHostState = remember { SnackbarHostState() }

    var menuExpanded by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var showAutoListDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    val showLabels = remember { mutableStateOf(true) }
    val textFieldSquared = remember { mutableStateOf(true)}
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showAboutScreen by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }

    // Push undo state onto stack
    fun pushUndoState() {
        val snapshot = currentListData.deepCopy()
        val lastSnapshot = undoStack.lastOrNull()
        if (lastSnapshot == snapshot) return
        else undoStack.addLast(snapshot)
    }

    // Undo button function
    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeLast()
            currentListData = previous
            scope.launch { dataStore.saveListData(currentListData) }
        }
    }

    // Create a brand-new list
    fun createNewList() {
        undoStack.clear()
        currentListData = ListData.newListData()
    }

    // Load last opened list
    LaunchedEffect(Unit) {
        val lastKey = dataStore.getLastOpenedKey()
        if (lastKey != null) {
            currentListData = dataStore.loadListData(lastKey)
        } else {
            createNewList()
        }
    }

    // Load saved settings
    LaunchedEffect(Unit) {
        val savedTheme = dataStore.getThemeMode()
        val savedColor = dataStore.getColorVariant()
        showLabels.value = dataStore.getShowLabels()
        textFieldSquared.value = dataStore.getTextFieldShape()
        onThemeChange(savedTheme, savedColor)
    }

    // Auto-save every change
    LaunchedEffect(
        currentListData.title.value,
        currentListData.items.filterIsInstance<ListItem.TaskItem>().map { it.textState.value },
        currentListData.checkedStates.toList(),
        savedLists
    ) {
        delay(300)
        scope.launch {
            dataStore.saveListData(currentListData)
        }
    }

    // Keep checkedStates in sync with task items
    LaunchedEffect(currentListData.items.size) {
        val tasks = currentListData.items.filterIsInstance<ListItem.TaskItem>()
        while (currentListData.checkedStates.size < tasks.size)
            currentListData.checkedStates.add(false)
        while (currentListData.checkedStates.size > tasks.size)
            currentListData.checkedStates.removeAt(currentListData.checkedStates.lastIndex)
    }

    // Set default list title when saving
    LaunchedEffect(showSaveDialog) {
        if (showSaveDialog) {
            inputListName = currentListData.title.value
        }
    }

    // Handle dynamic reordering
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            val tasks = currentListData.items.filterIsInstance<ListItem.TaskItem>().toMutableList()
            val movedTask = tasks.removeAt(from.index)
            tasks.add(to.index, movedTask)

            var taskIndex = 0
            currentListData.items.replaceAll { item ->
                if (item is ListItem.TaskItem) tasks[taskIndex++] else item
            }

            currentListData.checkedStates.add(to.index, currentListData.checkedStates.removeAt(from.index))
        },
        onDragEnd = { _, _ ->
            pushUndoState()
            scope.launch {
                dataStore.saveListData(currentListData)
            }
        }
    )

    // Voice manager
    val activity = LocalContext.current as Activity
    val voiceResult = remember { mutableStateOf("") }
    val voiceManager = remember { VoiceRecognitionManager(activity) }

    // Settings screen
    if (showSettingsScreen) {
        SettingsScreen(
            themeMode = themeMode,
            colorVariant = colorVariant,
            showLabels = showLabels.value,
            textFieldSquared = textFieldSquared.value,
            onBackClick = { showSettingsScreen = false },
            onThemeModeChange = { mode ->
                onThemeChange(mode, "")
            },
            onColorVariantChange = { color ->
                onThemeChange("", color)
            },
            onShowLabelsChange = { enabled ->
                showLabels.value = enabled
                scope.launch {
                    dataStore.saveShowLabels(enabled)
                }
            },
            onShapeChange = { enabled ->
                textFieldSquared.value = enabled
            }
        )
    } else if (showAboutScreen) {
        AboutScreen(
            onBackClick = { showAboutScreen = false }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
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
                        listData = currentListData,
                        colorScheme = colorScheme,
                        onVoiceInputClick = {
                            val permission = android.Manifest.permission.RECORD_AUDIO
                            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
                            } else {
                                showAutoListDialog = true
                            }
                        },
                        isRecording = isRecording,
                        snackbarHostState = snackbarHostState,
                        pushUndo = { pushUndoState() },
                        scope = scope
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
                        listData = currentListData,
                        dataStore = dataStore,
                        onUndo = { undo() },
                        menuExpanded = menuExpanded,
                        onMenuExpandChange = { menuExpanded = it },
                        onShowSaveDialog = { showSaveDialog = true },
                        onShowLoadDialog = { showLoadDialog = true },
                        onShowImportDialog = { showImportDialog = true },
                        onShowExportDialog = { showExportDialog = true },
                        onShowSettings = { showSettingsScreen = true },
                        onShowAbout = { showAboutScreen = true },
                        updateSavedLists = { savedLists = it },
                        onNewList = { createNewList() }
                    )
                    TaskListSection(
                        listData = currentListData,
                        scope = scope,
                        dataStore = dataStore,
                        state = state,
                        pushUndo = { pushUndoState() },
                        showLabels = showLabels.value,
                        textFieldSquared = textFieldSquared.value
                    )
                }

                // AutoList Dialog
                AutoListDialogBox(
                    showDialog = showAutoListDialog,
                    showLabels = showLabels.value,
                    isRecording = isRecording,
                    onDismiss = {
                        showAutoListDialog = false
                        isRecording = false
                    },
                    onStartRecording = {
                        isRecording = true
                        voiceManager.startListening(
                            onResult = { text ->
                                voiceResult.value = text.take(MAX_PROMPT_LENGTH)
                                isRecording = false
                            },
                            onError = { errorMsg ->
                                isRecording = false
                            }
                        )
                    },
                    onStopRecording = {
                        voiceManager.stopListening()
                        isRecording = false
                    },
                    transcribedText = voiceResult,
                    onNewList = { newList ->
                        undoStack.clear()
                        currentListData = newList
                    },
                    maxPromptLength = MAX_PROMPT_LENGTH
                )

                // Save Dialog
                SaveDialogBox(
                    showDialog = showSaveDialog,
                    showLabels = showLabels.value,
                    onDismiss = {
                        showSaveDialog = false
                        inputListName = ""
                    },
                    listData = currentListData,
                    inputListName = inputListName,
                    onInputChange = { inputListName = it },
                    dataStore = dataStore,
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )

                // Load Dialog
                LoadDialogBox(
                    showDialog = showLoadDialog,
                    onDismiss = { showLoadDialog = false },
                    savedLists = savedLists,
                    onLoad = { id ->
                        currentListData = dataStore.loadListData(id)
                        scope.launch { dataStore.saveLastOpenedKey(id) }
                    },
                    onDelete = { id ->
                        dataStore.deleteListById(id)
                        savedLists = savedLists.filterNot { it.first == id }
                        if (currentListData.id == id) {
                            currentListData.reset()
                            dataStore.saveLastOpenedKey(currentListData.id)
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )

                /*
                // Import Dialog
                ImportDialogBox(
                    showDialog = showImportDialog,
                    onDismiss = { showImportDialog = false },
                    onImport = {
                        // TODO: Add your import functionality here
                    },
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )

                // Export Dialog
                ExportDialogBox(
                    showDialog = showExportDialog,
                    onDismiss = { showExportDialog = false },
                    onExport = {
                        // TODO: Add your export functionality here
                    },
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                */
            }
        }
    }
}