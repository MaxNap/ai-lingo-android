package com.ailingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ailingo.app.ui.navigation.Routes
import com.ailingo.app.lesson.data.ProgressRepository
import kotlinx.coroutines.flow.collectLatest

private data class LearnListItem(
    val id: String,            // "1", "2", ...
    val title: String,         // e.g. "Getting Started"
    val tag: String,           // "Basics", "Intermediate"...
    val subtitle: String       // short description
)

// 🔹 Matches your Figma: same title, different level tag
private val learnItems = listOf(
    LearnListItem(
        id = "1",
        title = "Getting Started",
        tag = "Basics",
        subtitle = "Learn the fundamentals of AI prompting"
    ),
    LearnListItem(
        id = "2",
        title = "Getting Started",
        tag = "Intermediate",
        subtitle = "Build clearer, more powerful prompts"
    ),
    LearnListItem(
        id = "3",
        title = "Getting Started",
        tag = "Advanced",
        subtitle = "Use structure, roles, and context like a pro"
    ),
    LearnListItem(
        id = "4",
        title = "Getting Started",
        tag = "Expert",
        subtitle = "Master complex, multi-step prompt strategies"
    )
)

@Composable
fun LearnScreen(navController: NavController) {
    // --- listen to progress for this course and keep a simple map in state ---
    val repo = remember { ProgressRepository() }
    var done by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    LaunchedEffect(Unit) {
        // ProgressRepository stores docs like: users/{uid}/progress/courseA_lesson1
        // and emits a map like {"lesson1" to true}
        repo.progressMapForCourse("courseA").collectLatest { map ->
            done = map
        }
    }

    Scaffold(
        // bottomBar = { BottomNavBar(...) }  // if you have a global bottom bar, leave it in MainActivity
        containerColor = Color(0xFFF6F7FB) // soft background like Figma
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Text(
                text = "Learn AI Prompting",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Begin your journey with these lessons to master AI prompts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            // Levels list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(learnItems) { item ->
                    val lessonKey = "lesson${item.id}"          // "1" -> "lesson1"
                    val isCompleted = done[lessonKey] == true

                    LevelCard(
                        tag = item.tag,
                        title = item.title,
                        subtitle = item.subtitle,
                        completed = isCompleted,
                        onClick = {
                            // For now, every level goes to the Getting Started lesson overview.
                            // You can later map different unitIds if needed.
                            navController.navigate(Routes.lessonOverview("1"))
                        }

                    )
                }
            }
        }
    }
}

@Composable
private fun PillTag(text: String) {
    // Color-code chips by level
    val (bgColor, contentColor) = when (text) {
        "Basics" -> Pair(Color(0xFFE4F7E9), Color(0xFF1B8A3B))        // green
        "Intermediate" -> Pair(Color(0xFFE3F1FF), Color(0xFF246BCE))  // blue
        "Advanced" -> Pair(Color(0xFFFFF1E2), Color(0xFFB65A16))      // orange
        "Expert" -> Pair(Color(0xFFFEE5EA), Color(0xFFA7183A))        // red
        "Completed" -> Pair(Color(0xFFE6F4FF), Color(0xFF1565C0))     // blue-ish
        else -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = contentColor
        )
    }
}

@Composable
private fun LevelCard(
    tag: String,
    title: String,
    subtitle: String,
    completed: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (completed) {
        Color(0xFFE6F7ED) // light green tint when completed
    } else {
        Color(0xFFEDF4FF) // soft blue card like Figma
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PillTag(if (completed) "Completed" else tag)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
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
