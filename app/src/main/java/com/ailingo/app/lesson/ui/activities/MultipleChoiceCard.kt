package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.McqActivity
import com.ailingo.app.lesson.model.McqOption

@Composable
fun MultipleChoiceCard(
    act: McqActivity,
    onSelect: (McqOption) -> Unit,
    feedback: String?
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Text(act.question, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            act.options.forEach { opt ->
                Button(
                    onClick = { onSelect(opt) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) { Text(opt.text) }
            }
            if (feedback != null) {
                Spacer(Modifier.height(12.dp))
                Text(feedback, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
