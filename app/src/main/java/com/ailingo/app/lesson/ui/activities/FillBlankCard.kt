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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.FillBlankActivity

@Composable
fun FillBlankCard(
    act: FillBlankActivity,
    chosen: String?,
    onChoose: (String) -> Unit,
    feedback: String?
) {
    // Match style with MCQ / Match cards
    val cardBg = Color(0xFFF3F6FF)          // soft blue card background
    val borderBlue = Color(0xFF1AB8E2)      // turquoise outline
    val defaultText = Color(0xFF222222)
    val correctGreen = Color(0xFF2DBF6C)
    val incorrectRed = Color(0xFFFF6B6B)

    // We can infer correctness from chosen vs act.correct
    val isCorrectState = chosen != null && chosen == act.correct
    val isIncorrectState = chosen != null && chosen != act.correct

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
            // ----- Sentence with highlighted blank -----
            val placeholder = "_____"
            val sentence = act.sentenceWithBlank
            val idx = sentence.indexOf(placeholder)

            if (idx >= 0) {
                val before = sentence.substring(0, idx)
                val after = sentence.substring(idx + placeholder.length)

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = before,
                        style = MaterialTheme.typography.titleMedium,
                        color = defaultText
                    )
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = after,
                        style = MaterialTheme.typography.titleMedium,
                        color = defaultText
                    )
                }
            } else {
                // fallback if there's no "_____"
                Text(
                    text = sentence,
                    style = MaterialTheme.typography.titleMedium,
                    color = defaultText
                )
            }

            Spacer(Modifier.height(16.dp))

            // ----- Options as pill buttons -----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                act.options.forEach { opt ->
                    val shape = RoundedCornerShape(999.dp)

                    val isSelected = chosen == opt
                    val isCorrectOption = isSelected && opt == act.correct
                    val isWrongSelected = isSelected && opt != act.correct

                    val containerColor: Color
                    val contentColor: Color
                    val borderColor: Color

                    when {
                        isCorrectOption -> {
                            containerColor = correctGreen
                            contentColor = Color.White
                            borderColor = Color.Transparent
                        }
                        isWrongSelected -> {
                            containerColor = incorrectRed.copy(alpha = 0.9f)
                            contentColor = Color.White
                            borderColor = Color.Transparent
                        }
                        else -> {
                            containerColor = Color.White
                            contentColor = defaultText
                            borderColor = borderBlue
                        }
                    }

                    val isFilled = isCorrectOption || isWrongSelected

                    if (isFilled) {
                        Button(
                            onClick = { onChoose(opt) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            shape = shape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = contentColor
                            )
                        ) {
                            Text(opt)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onChoose(opt) },
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
                            Text(opt)
                        }
                    }
                }
            }

            // ----- Feedback row -----
            if (feedback != null) {
                Spacer(Modifier.height(16.dp))
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
        }
    }
}
