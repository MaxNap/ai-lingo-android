package com.ailingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import com.ailingo.app.ui.navigation.Routes

private data class LearnItem2(
    val id: String,            // e.g. "1"
    val title: String,         // e.g. "Getting Started"
    val tag: String = "Basics",
    val subtitle: String = "Learn the fundamentals of AI prompting"
)

private val learnItems = listOf(
    LearnItem2(id = "1", title = "Getting Started"),
    LearnItem2(id = "2", title = "Clear & Specific Prompts"),
    LearnItem2(id = "3", title = "Asking Simple Questions"),
    LearnItem2(id = "4", title = "Short Answers Practice")
)

@Composable
fun LearnScreen(navController: NavController) {
    Scaffold(
        bottomBar = { /* BottomNavBar() if you have one */ },
        containerColor = MaterialTheme.colorScheme.surface
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Learn AI Prompting",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Begin your journey with these lessons to master AI prompts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(learnItems) { item ->
                    LessonCard(
                        tag = item.tag,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            // Navigate based on lesson ID
                            when (item.id) {
                                // Getting Started → open Lesson 1 directly
                                "1" -> navController.navigate(Routes.Lesson1)

                                // Other lessons → open overview
                                else -> navController.navigate(Routes.lessonOverview(item.id))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PillTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun LessonCard(
    tag: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PillTag(tag)
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
