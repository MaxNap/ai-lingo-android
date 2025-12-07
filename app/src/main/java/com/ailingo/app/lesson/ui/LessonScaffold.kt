package com.ailingo.app.lesson.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onFinishLesson: (() -> Unit)? = null,
    isLessonCompleted: Boolean = false,
    syncing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    // Brand colors similar to Figma
    val headerBg = Color(0xFFEFF4FF)      // light blue top bar
    val accentPink = Color(0xFFCB39C3)    // Continue / outline color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB)) // soft page background
    ) {
        // ----- Top header strip -----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerBg)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "❤️ $hearts",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Progress bar under header
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // ----- Body -----
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )

        // ----- Bottom buttons -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back – outlined pink pill
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = accentPink
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.5.dp
                )
            ) {
                Text("Back")
            }

            // Continue – filled pink pill
            Button(
                onClick = onNext,
                enabled = isNextEnabled,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentPink,
                    contentColor = Color.White
                )
            ) {
                Text("Continue")
            }
        }

        // ----- Optional finish button -----
        if (isLessonCompleted && onFinishLesson != null) {
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onFinishLesson,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                enabled = !syncing,
                shape = MaterialTheme.shapes.extraLarge
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
            Spacer(Modifier.height(8.dp))
        }
    }
}
