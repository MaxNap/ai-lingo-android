package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.RecapActivity

@Composable
fun RecapCard(
    act: RecapActivity
) {
    val cardBg = Color(0xFFF3F6FF)   // same family as other lesson cards
    val titleColor = Color(0xFF222222)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header
            Text(
                text = "Lesson Recap",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = titleColor
            )

            Spacer(Modifier.height(10.dp))

            // Recap bullets
            act.bullets.forEach { line ->
                Text(
                    text = "• $line",
                    style = MaterialTheme.typography.bodyMedium,
                    color = titleColor
                )
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Reward section
            Text(
                text = "Your reward",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "🎁 +${act.rewardXp} XP   •   ${act.rewardStars}★",
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor
            )

            Spacer(Modifier.height(10.dp))

            // Hint for next lesson
            Text(
                text = "Next step",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "💡 ${act.nextLessonHint}",
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor
            )
        }
    }
}
