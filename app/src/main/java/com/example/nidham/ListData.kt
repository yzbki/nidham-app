package com.example.nidham

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class ListData {
    val title = mutableStateOf("To-Do List")
    val tasks = mutableStateListOf(TaskItem())
    val checkedStates = mutableStateListOf(false)

    fun reset() {
        title.value = "To-Do List"
        tasks.clear()
        checkedStates.clear()
        tasks.add(TaskItem())
        checkedStates.add(false)
    }
}