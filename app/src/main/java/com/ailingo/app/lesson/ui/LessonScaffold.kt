package com.ailingo.app.lesson.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LessonScaffold(
    title: String,
    hearts: Int,
    progress: Float,
    isNextEnabled: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    // Optional: you can pass this in from LessonViewModel later
    onFinishLesson: (() -> Unit)? = null,
    isLessonCompleted: Boolean = false,
    syncing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // ----- Top bar -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text("❤️ $hearts", style = MaterialTheme.typography.titleMedium)
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // ----- Body -----
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )

        // ----- Bottom buttons -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            Button(onClick = onNext, enabled = isNextEnabled) { Text("Continue") }
        }

        // ----- Optional finish button -----
        if (isLessonCompleted && onFinishLesson != null) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onFinishLesson,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !syncing
            ) {
                if (syncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Saving...")
                } else {
                    Text("Finish Lesson")
                }
            }
        }
    }
}
