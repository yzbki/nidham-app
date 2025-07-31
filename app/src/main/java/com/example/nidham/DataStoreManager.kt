package com.example.nidham

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "nidham_prefs")

class DataStoreManager(private val context: Context) {

    private val gson = Gson()

    private val LAST_OPENED_LIST_KEY = stringPreferencesKey("last_opened_list")

    suspend fun saveListData(listName: String, listData: ListData) {
        val tasksJson = gson.toJson(listData.tasks.map { it.textState.value })
        val checksJson = gson.toJson(listData.checkedStates)
        val title = listData.title.value

        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("${listName}_tasks")] = tasksJson
            prefs[stringPreferencesKey("${listName}_checked")] = checksJson
            prefs[stringPreferencesKey("${listName}_title")] = title
        }
    }

    suspend fun loadListData(listName: String): ListData {
        val prefs = context.dataStore.data.first()

        val listData = ListData()
        prefs[stringPreferencesKey("${listName}_title")]?.let { listData.title.value = it }

        val tasks = prefs[stringPreferencesKey("${listName}_tasks")]?.let { json ->
            gson.fromJson(json, Array<String>::class.java).toList()
        } ?: emptyList()

        val checks = prefs[stringPreferencesKey("${listName}_checked")]?.let { json ->
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

        while (listData.checkedStates.size < listData.tasks.size)
            listData.checkedStates.add(false)

        return listData
    }

    suspend fun deleteListByName(listName: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("${listName}_tasks"))
            prefs.remove(stringPreferencesKey("${listName}_checked"))
            prefs.remove(stringPreferencesKey("${listName}_title"))
        }
    }

    suspend fun getSavedListNames(): List<String> {
        val prefs = context.dataStore.data.first()
        return prefs.asMap()
            .keys
            .mapNotNull { it.name }
            .mapNotNull { name ->
                // Only take keys ending in _title, which uniquely identifies a saved list
                if (name.endsWith("_title")) name.removeSuffix("_title") else null
            }
            .distinct()
    }

    suspend fun saveLastOpenedKey(listKey: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_OPENED_LIST_KEY] = listKey
        }
    }

    suspend fun getLastOpenedKey(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[LAST_OPENED_LIST_KEY]
    }

}
