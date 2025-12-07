package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.MatchActivity

@Composable
fun MatchPairsCard(
    act: MatchActivity,
    currentMatches: Map<Int, String>,
    onPick: (rowIndex: Int, rightChoice: String) -> Unit,
    feedback: String?,
    // NEW: which wrong pair the user clicked (from ViewModel)
    wrongMatchRowIndex: Int?,
    wrongMatchChoice: String?
) {
    // Same palette as MultipleChoiceCard
    val cardBg = Color(0xFFF3F6FF)          // soft blue card background
    val borderBlue = Color(0xFF1AB8E2)      // turquoise outline
    val defaultText = Color(0xFF222222)
    val correctGreen = Color(0xFF2DBF6C)
    val incorrectRed = Color(0xFFFF6B6B)

    // Detect state from feedback text (matches ViewModel messages)
    val isAllCorrectState = feedback?.startsWith("Great matching") == true
    val isIncorrectState = feedback?.startsWith("❌") == true

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
            // Instruction text
            Text(
                text = act.instruction,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            // Each row: left phrase + right options as pills
            act.rows.forEachIndexed { rowIndex, row ->
                Text(
                    text = row.leftText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = defaultText
                )
                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val matchedChoice = currentMatches[rowIndex]

                    act.rightChoices.forEach { choice ->
                        val shape = RoundedCornerShape(999.dp)

                        // VM only stores correct matches in currentMatches,
                        // so anything there is a correct pair.
                        val isCorrectSelected = matchedChoice == choice

                        // Wrong pick = last clicked wrong choice from VM
                        val isWrongSelected =
                            wrongMatchRowIndex == rowIndex && wrongMatchChoice == choice

                        val containerColor: Color
                        val contentColor: Color
                        val borderColor: Color

                        when {
                            isCorrectSelected -> {
                                // GREEN pill
                                containerColor = correctGreen
                                contentColor = Color.White
                                borderColor = Color.Transparent
                            }
                            isWrongSelected -> {
                                // RED pill
                                containerColor = incorrectRed.copy(alpha = 0.9f)
                                contentColor = Color.White
                                borderColor = Color.Transparent
                            }
                            else -> {
                                // Default pill
                                containerColor = Color.White
                                contentColor = defaultText
                                borderColor = borderBlue
                            }
                        }

                        val enabled = matchedChoice == null || isCorrectSelected

                        if (isCorrectSelected || isWrongSelected) {
                            Button(
                                onClick = { onPick(rowIndex, choice) },
                                enabled = enabled,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                shape = shape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor
                                )
                            ) {
                                Text(choice)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onPick(rowIndex, choice) },
                                enabled = enabled,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                shape = shape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = contentColor
                                ),
                                border = BorderStroke(1.5.dp, borderColor)
                            ) {
                                Text(choice)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            // Feedback row
            if (feedback != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (icon, tint) = when {
                        isAllCorrectState -> Icons.Filled.CheckCircle to correctGreen
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
        }
    }
}
