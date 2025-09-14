package com.ailingo.app.lesson.ui.activities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ailingo.app.lesson.model.FreePromptActivity
import androidx.compose.ui.Alignment


@Composable
fun FreePromptPracticeCard(
    act: FreePromptActivity,
    userText: String,
    onChange: (String) -> Unit,
    onSubmit: () -> Unit,
    showMockReply: Boolean,
    feedback: String?
) {
    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Text(act.instruction, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = userText,
                onValueChange = onChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., What are some easy pasta recipes?") }
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onSubmit, modifier = Modifier.align(Alignment.End)) { Text("Submit") }

            if (feedback != null) {
                Spacer(Modifier.height(8.dp))
                Text(feedback, style = MaterialTheme.typography.bodyMedium)
            }

            if (showMockReply) {
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(12.dp))
                Text("AI Reply:", style = MaterialTheme.typography.titleMedium)
                Text(act.mockReply, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
