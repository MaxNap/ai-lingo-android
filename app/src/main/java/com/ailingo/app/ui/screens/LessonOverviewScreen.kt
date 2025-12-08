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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ailingo.app.lesson.data.ProgressRepository
import kotlinx.coroutines.flow.collectLatest

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
            // Custom header matching Figma (light blue band with back + title)
            Surface(
                color = Color(0xFFEAF2FF),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Getting Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
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
                        isPrimary = index == 0,          // first card = blue style
                        onClick = {
                            if (!unlocked) return@OverviewRowCard

                            // Navigate to the correct lesson.
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
        }
    }
}

@Composable
private fun OverviewRowCard(
    title: String,
    description: String,
    completed: Boolean,
    unlocked: Boolean,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    // Backgrounds:
    //  - Completed: soft green
    //  - First card: light blue
    //  - Others: light grey
    val bg = when {
        completed -> Color(0xFFDFF7DF)
        isPrimary -> Color(0xFFEAF2FF)
        else -> Color(0xFFF4F4F4)
    }

    val textColor = Color(0xFF222222)

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)         // bigger cards
            .clickable(enabled = unlocked) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = textColor
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.85f)
                )

                if (completed) {
                    Spacer(Modifier.height(12.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("Completed") },
                        leadingIcon = { Text("✓") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = textColor,
                            leadingIconContentColor = textColor
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
