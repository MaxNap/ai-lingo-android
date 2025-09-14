package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.RecapActivity

@Composable
fun RecapCard(
    act: RecapActivity
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Text("Lesson Recap", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            act.bullets.forEach {
                Text("• $it", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("🎁 Reward: +${act.rewardXp} XP, ${act.rewardStars}★", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(6.dp))
            Text("💡 ${act.nextLessonHint}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
