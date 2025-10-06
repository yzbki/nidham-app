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
import com.example.nidham.ui.components.TaskListSection
import com.example.nidham.ui.components.TopBarSection
import com.example.nidham.ui.components.VoiceDialogBox
import com.example.nidham.ui.theme.NidhamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }
    var inputListName by remember { mutableStateOf("") }
    var currentListData by remember { mutableStateOf(ListData()) }
    var savedLists by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val snackbarHostState = remember { SnackbarHostState() }

    var menuExpanded by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }

    // Helper to create a brand-new list
    fun createNewList() {
        currentListData = ListData.newListData()
    }

    // Load last opened list
    LaunchedEffect(Unit) {
        val lastKey = dataStore.getLastOpenedKey()
        if (lastKey != null) {
            currentListData = dataStore.loadListData(lastKey)
        }
    }

    // Auto-save on any change
    LaunchedEffect(
        currentListData.title.value,
        currentListData.tasks.map { it.textState.value },
        currentListData.checkedStates.toList(),
        savedLists
    ) {
        delay(300) // small debounce to avoid spamming saves
        scope.launch {
            dataStore.saveListData(currentListData)
        }
    }

    // Keep checkedStates in sync with tasks size
    LaunchedEffect(currentListData.tasks.size) {
        while (currentListData.checkedStates.size < currentListData.tasks.size)
            currentListData.checkedStates.add(false)
        while (currentListData.checkedStates.size > currentListData.tasks.size)
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
            currentListData.tasks.add(to.index, currentListData.tasks.removeAt(from.index))
            currentListData.checkedStates.add(to.index, currentListData.checkedStates.removeAt(from.index))
        },
        onDragEnd = { _, _ ->
            scope.launch {
                dataStore.saveListData(currentListData)
            }
        }
    )

    // Voice manager
    val activity = LocalContext.current as Activity
    val voiceResult = remember { mutableStateOf("") }
    val voiceManager = remember { VoiceRecognitionManager(activity) }

    // Main UI
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
                    listData = currentListData,
                    dataStore = dataStore,
                    menuExpanded = menuExpanded,
                    onMenuExpandChange = { menuExpanded = it },
                    onShowSaveDialog = { showSaveDialog = true },
                    onShowLoadDialog = { showLoadDialog = true },
                    updateSavedLists = { savedLists = it },
                    onNewList = { createNewList() }
                )
                TaskListSection(
                    listData = currentListData,
                    scope = scope,
                    dataStore = dataStore,
                    state = state
                )
            }

            // Voice Dialog
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
                transcribedText = voiceResult,
                listData = currentListData
            )

            // Save Dialog
            SaveDialogBox(
                showDialog = showSaveDialog,
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
        }
    }
}