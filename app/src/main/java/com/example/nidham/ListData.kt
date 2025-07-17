package com.example.nidham

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val textState: MutableState<String> = mutableStateOf("")
)

class ListData {
    val title = mutableStateOf("To-Do List")
    val tasks = mutableStateListOf(TaskItem())
    val checkedStates = mutableStateListOf<Boolean>()

    fun reset() {
        title.value = "To-Do List"
        tasks.clear()
        checkedStates.clear()
        tasks.add(TaskItem())
        checkedStates.add(false)
    }
}