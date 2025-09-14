package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.FillBlankActivity

@Composable
fun FillBlankCard(
    act: FillBlankActivity,
    chosen: String?,
    onChoose: (String) -> Unit,
    feedback: String?
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Text(act.sentenceWithBlank, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                act.options.forEach { opt ->
                    FilterChip(
                        selected = chosen == opt,
                        onClick = { onChoose(opt) },
                        label = { Text(opt) }
                    )
                }
            }
            if (feedback != null) {
                Spacer(Modifier.height(8.dp))
                Text(feedback, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
