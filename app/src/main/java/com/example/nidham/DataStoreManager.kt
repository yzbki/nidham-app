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
    private val LIST_IDS_KEY = stringPreferencesKey("list_ids") // stores all list IDs

    suspend fun saveListData(listData: ListData) {
        val tasksJson = gson.toJson(listData.tasks.map { it.textState.value })
        val checksJson = gson.toJson(listData.checkedStates)
        val title = listData.title.value.trim()

        if (title.isEmpty()) {
            throw IllegalArgumentException("List title cannot be empty")
        }

        context.dataStore.edit { prefs ->
            // Save list content using list ID
            prefs[stringPreferencesKey("${listData.id}_tasks")] = tasksJson
            prefs[stringPreferencesKey("${listData.id}_checked")] = checksJson
            prefs[stringPreferencesKey("${listData.id}_title")] = listData.title.value

            // Maintain a list of all IDs
            val currentIds = prefs[LIST_IDS_KEY]?.split(",")?.toMutableSet() ?: mutableSetOf()
            currentIds.add(listData.id)
            prefs[LIST_IDS_KEY] = currentIds.joinToString(",")
        }
    }

    suspend fun loadListData(listId: String): ListData {
        val prefs = context.dataStore.data.first()
        val listData = ListData()

        prefs[stringPreferencesKey("${listId}_title")]?.let { listData.title.value = it }

        val tasks = prefs[stringPreferencesKey("${listId}_tasks")]?.let { json ->
            gson.fromJson(json, Array<String>::class.java).toList()
        } ?: emptyList()

        val checks = prefs[stringPreferencesKey("${listId}_checked")]?.let { json ->
            gson.fromJson(json, Array<Boolean>::class.java).toList()
        } ?: emptyList()

        listData.tasks.clear()
        listData.checkedStates.clear()

        tasks.forEach { taskText ->
            listData.tasks.add(TaskItem(textState = mutableStateOf(taskText)))
        }

        checks.forEach { listData.checkedStates.add(it) }

        while (listData.checkedStates.size < listData.tasks.size)
            listData.checkedStates.add(false)

        return listData
    }

    suspend fun deleteListById(listId: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("${listId}_tasks"))
            prefs.remove(stringPreferencesKey("${listId}_checked"))
            prefs.remove(stringPreferencesKey("${listId}_title"))

            // Remove from saved IDs
            val currentIds = prefs[LIST_IDS_KEY]?.split(",")?.toMutableSet() ?: mutableSetOf()
            currentIds.remove(listId)
            prefs[LIST_IDS_KEY] = currentIds.joinToString(",")
        }
    }

    suspend fun getSavedLists(): Map<String, String> {
        // Returns map of listId -> title
        val prefs = context.dataStore.data.first()
        val ids = prefs[LIST_IDS_KEY]?.split(",") ?: emptyList()
        val result = mutableMapOf<String, String>()
        ids.forEach { id ->
            prefs[stringPreferencesKey("${id}_title")]?.let { title ->
                result[id] = title
            }
        }
        return result
    }

    suspend fun saveLastOpenedKey(listId: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_OPENED_LIST_KEY] = listId
        }
    }

    suspend fun getLastOpenedKey(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[LAST_OPENED_LIST_KEY]
    }
}
