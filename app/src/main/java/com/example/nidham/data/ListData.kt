package com.example.nidham.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.structuralEqualityPolicy
import java.util.UUID

sealed class ListItem {
    abstract val id: String
    abstract val textState: MutableState<String>

    data class TaskItem(
        override val id: String = UUID.randomUUID().toString(),
        override val textState: MutableState<String> = mutableStateOf("")
    ) : ListItem()

    /* TODO: implement group creation
    data class GroupItem(
        override val id: String = UUID.randomUUID().toString(),
        override val textState: MutableState<String> = mutableStateOf(""),
        val color: MutableState<String> = mutableStateOf("#FFFFFF"),
        val collapsed: MutableState<Boolean> = mutableStateOf(false),
        val children: SnapshotStateList<TaskItem> = mutableStateListOf()
    ) : ListItem()
     */
}

class ListData(var id: String = UUID.randomUUID().toString()) {
    val title = mutableStateOf("")
    val items = mutableStateListOf<ListItem>()
    val checkedStates = mutableStateListOf<Boolean>()
    val selectAll = mutableStateOf(false)

    companion object {
        const val MAX_TITLE_LENGTH = 50
        const val MAX_TASKS = 100
        const val MAX_TASK_LENGTH = 200

        fun newListData(): ListData = ListData().apply {
            items.clear()
            checkedStates.clear()
            items.add(ListItem.TaskItem())
            checkedStates.add(false)
            selectAll.value = false
        }
    }

    fun reset() {
        title.value = ""
        items.clear()
        checkedStates.clear()
        items.add(ListItem.TaskItem())
        checkedStates.add(false)
        selectAll.value = false
    }

    fun copyId(existingId: String): ListData = ListData(existingId).also {
        it.title.value = this.title.value
        it.items.addAll(this.items)
        it.checkedStates.addAll(this.checkedStates)
    }

    suspend fun isTitleValid(
        title: String,
        dataStore: DataStoreManager,
        currentId: String? = null
    ): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_TITLE_LENGTH) return false
        if (dataStore.isTitleDuplicate(trimmed, excludeId = currentId)) return false
        return true
    }

    fun canAddTask(): Boolean {
        val taskCount = items.count { it is ListItem.TaskItem }
        return taskCount < MAX_TASKS
    }

    fun addTask(): Boolean {
        return if (canAddTask()) {
            items.add(ListItem.TaskItem())
            checkedStates.add(false)
            selectAll.value = false
            true
        } else {
            false
        }
    }

    fun allChecked(): Boolean {
        if(items.isEmpty()) return false
        else return checkedStates.all { it }
    }

    fun allEmpty(): Boolean {
        return items.all { it.textState.value.isEmpty() }
    }

    fun deepCopy(): ListData {
        val copy = ListData()
        copy.id = this.id
        copy.title.value = this.title.value

        copy.items.clear()
        this.items.forEach { item ->
            when (item) {
                is ListItem.TaskItem -> {
                    copy.items.add(
                        ListItem.TaskItem(
                            id = item.id,
                            textState = mutableStateOf(item.textState.value)
                        )
                    )
                }
            }
        }

        copy.checkedStates.clear()
        copy.checkedStates.addAll(this.checkedStates)

        copy.selectAll.value = this.selectAll.value

        return copy
    }
}