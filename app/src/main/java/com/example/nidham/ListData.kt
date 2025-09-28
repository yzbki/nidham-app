package com.example.nidham

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val textState: MutableState<String> = mutableStateOf("")
)

class ListData(val id: String = UUID.randomUUID().toString()) {
    val title = mutableStateOf("")
    val tasks = mutableStateListOf<TaskItem>()
    val checkedStates = mutableStateListOf<Boolean>()

    companion object {
        const val MAX_TITLE_LENGTH = 50

        fun newListData(): ListData {
            return ListData().apply {
                tasks.clear()
                checkedStates.clear()
                tasks.add(TaskItem())
                checkedStates.add(false)
            }
        }
    }

    fun reset() {
        title.value = ""
        tasks.clear()
        checkedStates.clear()
        tasks.add(TaskItem())
        checkedStates.add(false)
    }

    fun copyId(existingId: String): ListData {
        return ListData(existingId).also {
            it.title.value = this.title.value
            it.tasks.addAll(this.tasks)
            it.checkedStates.addAll(this.checkedStates)
        }
    }

    suspend fun isTitleValid(
        title: String,
        dataStore: DataStoreManager,
        currentId: String? = null
    ): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.length > MAX_TITLE_LENGTH) return false
        if (dataStore.isTitleDuplicate(trimmed, excludeId = currentId)) return false
        return true
    }
}