package com.ailingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import com.ailingo.app.lesson.data.ProgressRepository
import kotlinx.coroutines.flow.collectLatest

// --- Add an id so we can match progress docs like courseA_<id> ---
private data class SubLesson(
    val id: String,
    val title: String,
    val description: String
)

private val courseId = "courseA" // adjust to your real course id

private val gettingStarted = listOf(
    SubLesson("lesson1", "Lesson 1: What is a prompt?", "Understand what a prompt is and how it guides AI responses."),
    SubLesson("lesson2", "Lesson 2: Clear and Specific Prompts", "Discover how clarity, detail, and context can transform AI outputs."),
    SubLesson("lesson3", "Lesson 3: Asking Simple Questions", "Learn to ask clear, direct questions so AI knows exactly what you need."),
    SubLesson("lesson4", "Lesson 4: Short Answers Practice", "Practice when and how to ask for brief responses from AI.")
)

@Composable
fun LessonOverviewScreen(
    navController: NavController,
    lessonId: String
) {
    val lessons = gettingStarted

    // --- Listen to Firestore progress map for this course ---
    val repo = remember { ProgressRepository() }
    // Map<lessonId, completed>
    val progressMap by produceState(initialValue = emptyMap<String, Boolean>(), repo) {
        // Convert Flow to state without exposing coroutine boilerplate in the composable
        repo.progressMapForCourse(courseId).collectLatest { value ->
            value.also { value } // no-op for lint
            value.let { this@produceState.value = it }
        }
    }

    Scaffold(
        topBar = {
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
                itemsIndexed(lessons, key = { _, it -> it.id }) { _, item ->
                    val completed = progressMap[item.id] == true
                    OverviewRowCard(
                        title = item.title,
                        description = item.description,
                        completed = completed
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
    description: String,
    completed: Boolean
) {
    val bg = if (completed)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.surface

    val onBg = if (completed)
        MaterialTheme.colorScheme.onSecondaryContainer
    else
        MaterialTheme.colorScheme.onSurface

    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        color = bg, // ← change background by completion
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = onBg
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = onBg.copy(alpha = 0.8f)
            )
            if (completed) {
                Spacer(Modifier.height(8.dp))
                AssistChip(
                    onClick = { /* no-op */ },
                    label = { Text("Completed") },
                    leadingIcon = { Text("✓") },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = onBg,
                        leadingIconContentColor = onBg
                    )
                )
            }
        }
    }
}
