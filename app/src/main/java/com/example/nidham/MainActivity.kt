package com.example.nidham

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nidham.ui.screens.ToDoListScreen
import com.example.nidham.ui.theme.NidhamTheme

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