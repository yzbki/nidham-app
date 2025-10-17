package com.example.nidham.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    BackHandler {
        onBackClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = colorScheme.onBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "About",
                        style = typography.headlineMedium.copy(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // About Page Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    // About the App
                    Text(
                        text = "About Nidham",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Nidham is a versatile checklist management application designed to help you bring order to your daily life. Whether it's organizing your daily agenda, creating grocery lists, tracking workouts, or managing procedural tasks, Nidham offers a dynamic, intuitive experience to make list management effortless.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Features
                    Text(
                        text = "Features",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "• Dynamic Lists: Easily edit titles, tasks, and the order of items, with support for multi-delete.\n" +
                                "• Autosave & Autoload: Never worry about losing your list, your last opened list loads automatically.\n" +
                                "• AI List Generator: Generate lists from text prompts, from workouts and recipes to itineraries and more.\n" +
                                "• Voice Input: Record and transcribe prompts for an even faster list creation experience.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // About the Developer
                    Text(
                        text = "About the Developer",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Nidham was developed by myself, Mustafa Al-Youzbaki, published under my personal brand, Youzbaki Co. I created this app during my final year of university to improve how I manage my own lists and daily routines. The name \"Nidham\" comes from the Arabic word for \"system\" or \"order\", reflecting the app’s mission to help users organize their lives efficiently.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Connect with the Developer
                    Text(
                        text = "Connect with Me",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "• Website: www.yzbki.com\n" +
                                "• Instagram: @musalyouzbaki\n" +
                                "• LinkedIn: www.linkedin.com/in/mus-alyouzbaki/",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Optional Donation Section
                    Text(
                        text = "Support Development",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "If you enjoy using Nidham and want to support ongoing development, consider a donation in the future. Your support helps me continue improving the app and adding new features.",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}