package com.ailingo.app.ui.screens

import android.R.attr.enabled
import android.R.attr.onClick
import android.R.attr.subtitle
import android.R.attr.tag
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.core.graphics.alpha
import com.ailingo.app.ui.navigation.Routes
import com.ailingo.app.ui.screens.LessonCard
import com.ailingo.app.ui.screens.PillTag

// Lesson Section Class
private data class LearnItem(
    val id: String,                 // e.g. "1"
    val title: String,              // e.g. "Getting Started"
    val tag: String = "Basics",
    val subtitle: String = "Learn the fundamentals of AI prompting",
    val isLocked: Boolean = false,
    val prerequisite: String? = "",
    val isComplete: Boolean = false
)

// Lesson Sections
private val lessonLevels = listOf(
    // Basics (lvl 1)
    LearnItem(
        id = "1",
        title = "Getting Started",
        tag = "Basics",
        subtitle = "Learn the fundamentals of AI prompting",
        isLocked = false,
        prerequisite = null,
        isComplete = true
    ),
    // Next Steps (lvl 2)
    LearnItem(
        id = "2",
        title = "Next Steps",
        tag = "Level 2",
        subtitle = "Explore more advanced uses of AI",
        isLocked = true,
        prerequisite = "1",
        isComplete = false
    ),
    // Different AI Fields (lvl 3)
    LearnItem(
        id = "3",
        title = "Exploring AI in Different Fields",
        tag = "Advanced",
        subtitle = "Explore some of the different uses for AI",
        isLocked = true,
        prerequisite = "2",
        isComplete = false
    )
)

@Composable
private fun LessonCard(
    tag: String,
    title: String,
    subtitle: String,
    isLocked: Boolean,
    isComplete: Boolean,
    onClick: () -> Unit

) {
    // Colour for Locked Status
    val surfaceColor = if (isLocked) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    val tonalElevation = if (isLocked) 0.dp else 2.dp
    val tagText = if (isLocked) "Locked" else tag
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        tonalElevation = tonalElevation,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable (enabled = !isLocked) { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PillTag(
                text = tagText,
                isLocked = isLocked,
                isComplete = isComplete
            )
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
@Composable
private fun PillTag(text: String, isLocked: Boolean, isComplete: Boolean) {
    // isLocked Condition
    // Background Color
    val backgroundColor = if (isLocked) {
        Color.Gray.copy(alpha = 0.25f)
    } else if (isComplete) {
        Color.Green
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    // Text Color
    val textColor = if (isLocked) {
        Color.Gray
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    // isComplete Condition
    // Border Color (Testing if I wanted to use a border color)
    val borderColor = if (isComplete) {
        Color.Transparent
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun LearnScreen1(navController: NavController) {
    Scaffold() {
        inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // HEADER
            // Page Title
            Spacer(Modifier.height(24.dp))
            val offset = Offset(5.0f, 10.0f)
            Text(
                text = "Learn AI Prompting",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = Color(0xFF1AB8E2),
                        offset = offset,
                        blurRadius = 5f
                    )),

            )
            //Page Subtitle
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Begin your journey with these lessons to master AI prompts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 3.dp,
                color = Color(0xFFCB39C3))
            Spacer(Modifier.height(32.dp))

            // BODY
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                items(lessonLevels) { item ->
                    LessonCard(
                        tag = item.tag,
                        title = item.title,
                        subtitle = item.subtitle,
                        isLocked = item.isLocked,
                        isComplete = item.isComplete,
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



