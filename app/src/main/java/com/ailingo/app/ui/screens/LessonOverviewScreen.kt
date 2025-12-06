package com.ailingo.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ailingo.app.lesson.data.ProgressRepository
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.remember


// --- Simple model for each sub-lesson row ---
private data class SubLesson(
    val id: String,          // e.g. "lesson1"
    val title: String,
    val description: String
)

private val courseId = "courseA" // adjust to your real course id

private val gettingStarted = listOf(
    SubLesson(
        "lesson1",
        "Lesson 1: What is a prompt?",
        "Understand what a prompt is and how it guides AI responses."
    ),
    SubLesson(
        "lesson2",
        "Lesson 2: Clear and Specific Prompts",
        "Discover how clarity, detail, and context can transform AI outputs."
    ),
    SubLesson(
        "lesson3",
        "Lesson 3: Asking Simple Questions",
        "Learn to ask clear, direct questions so AI knows exactly what you need."
    ),
    SubLesson(
        "lesson4",
        "Lesson 4: Short Answers Practice",
        "Practice when and how to ask for brief responses from AI."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonOverviewScreen(
    navController: NavController,
    lessonId: String      // treated as unitId for now, e.g. "1"
) {
    // If you add more units later, you can switch on lessonId
    val lessons = when (lessonId) {
        "1" -> gettingStarted
        else -> gettingStarted
    }

    // --- Listen to Firestore progress map for this course ---
    val repo = remember { ProgressRepository() }
    // Map<lessonId, completed>
    val progressMap by produceState(initialValue = emptyMap<String, Boolean>(), key1 = repo) {
        repo.progressMapForCourse(courseId).collectLatest { value ->
            this.value = value
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Getting Started",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Start with the basics and unlock more lessons as you go.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = true)
            ) {
                itemsIndexed(lessons, key = { _, it -> it.id }) { index, item ->
                    val completed = progressMap[item.id] == true

                    // Unlock rule:
                    //  - Lesson 1 always unlocked
                    //  - Lesson N unlocked only if previous lesson is completed
                    val unlocked = if (index == 0) {
                        true
                    } else {
                        val previousId = lessons[index - 1].id
                        progressMap[previousId] == true
                    }

                    OverviewRowCard(
                        title = item.title,
                        description = item.description,
                        completed = completed,
                        unlocked = unlocked,
                        onClick = {
                            if (!unlocked) return@OverviewRowCard

                            // Navigate to the correct lesson.
                            // Right now only Lesson 1 exists.
                            when (item.id) {
                                "lesson1" -> navController.navigate("lesson/1/1")
                                // "lesson2" -> navController.navigate("lesson/1/2")
                                // "lesson3" -> navController.navigate("lesson/1/3")
                                // "lesson4" -> navController.navigate("lesson/1/4")
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("lesson/1/1") },
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
    completed: Boolean,
    unlocked: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        completed -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val onBg = when {
        completed -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        color = bg,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = unlocked) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
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

            if (!unlocked) {
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
