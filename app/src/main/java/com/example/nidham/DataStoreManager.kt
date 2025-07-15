package com.example.nidham

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "nidham_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val TASKS_KEY = stringPreferencesKey("tasks")
        val CHECKED_KEY = stringPreferencesKey("checked_states")
        val TITLE_KEY = stringPreferencesKey("title")
    }

    private val gson = Gson()

    suspend fun saveListData(listData: ListData) {
        val tasksJson = gson.toJson(listData.tasks.map { it.textState.value })
        val checksJson = gson.toJson(listData.checkedStates)
        val title = listData.title.value

        context.dataStore.edit { prefs ->
            prefs[TASKS_KEY] = tasksJson
            prefs[CHECKED_KEY] = checksJson
            prefs[TITLE_KEY] = title
        }
    }

    suspend fun loadListData(): ListData {
        val prefs = context.dataStore.data.first()

        val listData = ListData()
        prefs[TITLE_KEY]?.let { listData.title.value = it }

        val tasks = prefs[TASKS_KEY]?.let { json ->
            gson.fromJson(json, Array<String>::class.java).toList()
        } ?: emptyList()

        val checks = prefs[CHECKED_KEY]?.let { json ->
            gson.fromJson(json, Array<Boolean>::class.java).toList()
        } ?: emptyList()

        listData.tasks.clear()
        listData.checkedStates.clear()

        tasks.forEach { taskText ->
            listData.tasks.add(TaskItem(textState = mutableStateOf(taskText)))
        }

        checks.forEach {
            listData.checkedStates.add(it)
        }

        // Pad checkedStates if needed
        while (listData.checkedStates.size < listData.tasks.size)
            listData.checkedStates.add(false)

        return listData
    }
}
