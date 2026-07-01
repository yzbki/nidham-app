package com.youzbaki.nidham.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.youzbaki.nidham.BuildConfig
import com.youzbaki.nidham.service.SoundManager

@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

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
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            SoundManager.playButton(context)
                            onBackClick()
                        },
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // App name
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nidham",
                            style = typography.headlineSmall.copy(
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold),
                            color = colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Version ${BuildConfig.VERSION_NAME}",
                                style = typography.labelMedium,
                                color = colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = colorScheme.outlineVariant)

                    // App description
                    AboutSection(title = "About Nidham") {
                        Text(
                            text = "Nidham is a checklist management app designed to bring " +
                                    "order to your daily life. Whether you're organizing your agenda, " +
                                    "creating grocery lists, tracking workouts, or managing projects, " +
                                    "Nidham makes list management dynamic, intuitive, and effortless.",
                            style = typography.bodyMedium,
                            color = colorScheme.onSurface
                        )
                    }

                    // Feature list
                    AboutSection(title = "Features") {
                        FeatureRow(
                            title = "Dynamic Lists",
                            description = "Reorder tasks with a long press, check off items, and multi-delete with ease."
                        )
                        FeatureRow(
                            title = "Autosave & Autoload",
                            description = "Your lists save automatically, and the last list you opened loads right back up."
                        )
                        FeatureRow(
                            title = "AI List Generator",
                            description = "Generate lists from text prompts, from workouts and recipes to itineraries and more."
                        )
                        FeatureRow(
                            title = "Voice Input",
                            description = "Record and transcribe prompts for an even faster list creation experience."
                        )
                    }

                    // Developer description
                    AboutSection(title = "About the Developer") {
                        Text(
                            text = "Nidham was developed by Mustafa Al-Youzbaki, and published under " +
                                    "Youzbaki Co. I built this app in my final year of university " +
                                    "to better manage my tasks and daily routine. The name \"Nidham\" comes " +
                                    "from the Arabic word for \"system\" or \"order\", reflecting the app's " +
                                    "mission to help you organize your life efficiently.",
                            style = typography.bodyMedium,
                            color = colorScheme.onSurface
                        )
                    }

                    // Contact section
                    AboutSection(title = "Connect with Me") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ContactRow(
                                icon = Icons.Default.Language,
                                label = "yzbki.com",
                                onClick = { uriHandler.openUri("https://www.yzbki.com") }
                            )
                            ContactRow(
                                icon = Icons.Default.Code,
                                label = "github.com/yzbki",
                                onClick = { uriHandler.openUri("https://github.com/yzbki") }
                            )
                            ContactRow(
                                icon = Icons.Default.Link,
                                label = "linkedin.com/in/mus-alyouzbaki",
                                onClick = { uriHandler.openUri("https://www.linkedin.com/in/mus-alyouzbaki/") }
                            )
                        }
                    }

                    // Privacy policy
                    AboutSection(title = "Privacy Policy") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ContactRow(
                                icon = Icons.Default.Security,
                                label = "View Privacy Policy",
                                onClick = { uriHandler.openUri("https://www.yzbki.com/nidham-privacy-policy") }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A titled section used to group related content on the About screen.
 */
@Composable
private fun AboutSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colorScheme.onBackground
        )
        Column(content = content)
    }
}

/**
 * A single bolded feature title with a description underneath
 */
@Composable
private fun FeatureRow(
    title: String,
    description: String
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colorScheme.onSurface
        )
        Text(
            text = description,
            style = typography.bodyMedium,
            color = colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

/**
 * A tappable row with an icon and label, used for the developer's
 * website and social links. Opens the link in the browser/app on click.
 */
@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.onBackground,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = typography.bodyMedium,
            color = colorScheme.onSurface
        )
    }
}