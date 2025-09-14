package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IntroCard(
    heading: String,
    bullets: List<String>
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(heading, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            bullets.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
