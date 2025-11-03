package com.ailingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape

private data class SubLesson(val title: String, val description: String)

private val gettingStarted = listOf(
    SubLesson("Lesson 1: What is a prompt?", "Understand what a prompt is and how it guides AI responses."),
    SubLesson("Lesson 2: Clear and Specific Prompts", "Discover how clarity, detail, and context can transform AI outputs."),
    SubLesson("Lesson 3: Asking Simple Questions", "Learn to ask clear, direct questions so AI knows exactly what you need."),
    SubLesson("Lesson 4: Short Answers Practice", "Practice when and how to ask for brief responses from AI.")
)

@Composable
fun LessonOverviewScreen(
    navController: NavController,
    lessonId: String
) {
    val lessons = gettingStarted // can be dynamic later

    Scaffold(
        topBar = {
            // Simple title without icon
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Getting Started",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                itemsIndexed(lessons) { _, item ->
                    OverviewRowCard(
                        title = item.title,
                        description = item.description
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    // Start the first lesson (adjust route if needed)
                    navController.navigate("lesson/1/1")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Start Lesson 1")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OverviewRowCard(
    title: String,
    description: String
) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
