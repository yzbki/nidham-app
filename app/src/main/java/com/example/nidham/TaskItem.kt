package com.example.nidham

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val textState: MutableState<String> = mutableStateOf("")
)