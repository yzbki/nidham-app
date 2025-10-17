package com.example.nidham.data

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
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    private val COLOR_VARIANT_KEY = stringPreferencesKey("color_variant")

    suspend fun saveListData(listData: ListData) {
        val title = listData.title.value
        if (!listData.isTitleValid(title, this, currentId = listData.id)) return

        val tasksJson = gson.toJson(
            listData.items.filterIsInstance<ListItem.TaskItem>().map { it.textState.value }
        )
        val checksJson = gson.toJson(listData.checkedStates)

        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("${listData.id}_tasks")] = tasksJson
            prefs[stringPreferencesKey("${listData.id}_checked")] = checksJson
            prefs[stringPreferencesKey("${listData.id}_title")] = title
        }
    }

    suspend fun loadListData(listId: String): ListData {
        val prefs = context.dataStore.data.first()
        val listData = ListData().apply {
            this.title.value = prefs[stringPreferencesKey("${listId}_title")] ?: ""
        }

        val tasks = prefs[stringPreferencesKey("${listId}_tasks")]?.let { json ->
            gson.fromJson(json, Array<String>::class.java).toList()
        } ?: emptyList()

        val checks = prefs[stringPreferencesKey("${listId}_checked")]?.let { json ->
            gson.fromJson(json, Array<Boolean>::class.java).toList()
        } ?: emptyList()

        listData.items.clear()
        listData.checkedStates.clear()

        tasks.forEach { taskText ->
            listData.items.add(ListItem.TaskItem(textState = mutableStateOf(taskText)))
        }

        checks.forEach { listData.checkedStates.add(it) }

        while (listData.checkedStates.size < listData.items.filterIsInstance<ListItem.TaskItem>().size) {
            listData.checkedStates.add(false)
        }

        // Ensure at least one task exists
        if (listData.items.none { it is ListItem.TaskItem }) {
            listData.items.add(ListItem.TaskItem(textState = mutableStateOf("")))
            listData.checkedStates.add(false)
        }

        return listData.copyId(listId)
    }

    suspend fun deleteListById(listId: String) {
        context.dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("${listId}_tasks"))
            prefs.remove(stringPreferencesKey("${listId}_checked"))
            prefs.remove(stringPreferencesKey("${listId}_title"))
        }
    }

    suspend fun getSavedLists(): List<Pair<String, String>> {
        val prefs = context.dataStore.data.first()
        return prefs.asMap()
            .keys
            .mapNotNull { it.name }
            .mapNotNull { name ->
                if (name.endsWith("_title")) {
                    val id = name.removeSuffix("_title")
                    val title = prefs[stringPreferencesKey(name)] ?: ""
                    id to title
                } else null
            }
            .distinctBy { it.first } // avoid duplicates
    }

    suspend fun isTitleDuplicate(title: String, excludeId: String? = null): Boolean {
        val existingLists = getSavedLists()
        return existingLists.any { (id, existingTitle) ->
            id != excludeId && existingTitle.equals(title.trim(), ignoreCase = true)
        }
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

    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode
        }
    }

    suspend fun saveColorVariant(variant: String) {
        context.dataStore.edit { prefs ->
            prefs[COLOR_VARIANT_KEY] = variant
        }
    }

    suspend fun getThemeMode(): String {
        val prefs = context.dataStore.data.first()
        return prefs[THEME_MODE_KEY] ?: "system"
    }

    suspend fun getColorVariant(): String {
        val prefs = context.dataStore.data.first()
        return prefs[COLOR_VARIANT_KEY] ?: "default"
    }
}