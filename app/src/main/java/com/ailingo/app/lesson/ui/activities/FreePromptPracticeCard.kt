package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.FreePromptActivity
import androidx.compose.material3.Surface


@Composable
fun FreePromptPracticeCard(
    act: FreePromptActivity,
    userText: String,
    onChange: (String) -> Unit,
    onSubmit: () -> Unit,
    showMockReply: Boolean,
    feedback: String?
) {
    val cardBg = Color(0xFFF3F6FF)          // soft blue, same family as other cards
    val defaultText = Color(0xFF222222)
    val correctGreen = Color(0xFF2DBF6C)
    val incorrectRed = Color(0xFFFF6B6B)

    // Treat "success" as when we show the mock reply
    val isCorrectState = showMockReply
    // Treat "error" as when feedback starts with a hint or ❌
    val isIncorrectState = feedback?.startsWith("❌") == true || feedback == act.hint

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
            // Instruction
            Text(
                text = act.instruction,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = defaultText
            )

            Spacer(Modifier.height(12.dp))

            // User prompt input
            OutlinedTextField(
                value = userText,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    // "Right answer" example
                    Text("What are some easy pasta recipes?")
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = false,
                minLines = 3
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Submit")
            }

            // Feedback under the button
            if (feedback != null) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (icon, tint) = when {
                        isCorrectState -> Icons.Filled.CheckCircle to correctGreen
                        isIncorrectState -> Icons.Filled.ErrorOutline to incorrectRed
                        else -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Mock AI reply
            if (showMockReply) {
                Spacer(Modifier.height(16.dp))
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "AI Reply",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = act.mockReply,
                        style = MaterialTheme.typography.bodyMedium,
                        color = defaultText,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
