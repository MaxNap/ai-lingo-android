package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.McqActivity
import com.ailingo.app.lesson.model.McqOption

@Composable
fun MultipleChoiceCard(
    act: McqActivity,
    onSelect: (McqOption) -> Unit,
    feedback: String?,
    selectedOptionText: String? = null   // <- NEW: which answer user tapped
) {
    // Figma-like colors
    val cardBg = Color(0xFFF3F6FF)          // soft blue card
    val borderBlue = Color(0xFF1AB8E2)      // outline for default pills
    val defaultText = Color(0xFF222222)     // dark text
    val correctGreen = Color(0xFF2DBF6C)
    val incorrectRed = Color(0xFFFF6B6B)

    // 🟢 / 🔴 detection based on the actual feedback strings
    val isCorrectState = feedback == act.feedbackCorrect
    val isIncorrectState = feedback == act.feedbackIncorrect

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = act.question,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            act.options.forEach { opt ->
                val shape = RoundedCornerShape(999.dp)

                // Decide visual state for this option
                val isCorrectOption = isCorrectState && opt.correct
                val isWrongSelected =
                    isIncorrectState && selectedOptionText != null && opt.text == selectedOptionText

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
                        containerColor = incorrectRed
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
                        onClick = { onSelect(opt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = shape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = containerColor,
                            contentColor = contentColor
                        )
                    ) {
                        Text(opt.text)
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSelect(opt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = shape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = contentColor
                        ),
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Text(opt.text)
                    }
                }
            }

            // Feedback row
            if (feedback != null) {
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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
