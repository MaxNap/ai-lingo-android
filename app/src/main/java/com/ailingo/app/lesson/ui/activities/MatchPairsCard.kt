package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.MatchActivity

@Composable
fun MatchPairsCard(
    act: MatchActivity,
    currentMatches: Map<Int, String>,
    onPick: (rowIndex: Int, rightChoice: String) -> Unit,
    feedback: String?
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Text(act.instruction, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            act.rows.forEachIndexed { i, row ->
                Text(row.leftText, style = MaterialTheme.typography.bodyLarge)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    act.rightChoices.forEach { choice ->
                        val selected = currentMatches[i] == choice
                        OutlinedButton(
                            onClick = { onPick(i, choice) },
                            enabled = currentMatches[i] == null || selected,
                        ) {
                            Text(if (selected) "✅ $choice" else choice)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            if (feedback != null) {
                Spacer(Modifier.height(8.dp))
                Text(feedback, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
