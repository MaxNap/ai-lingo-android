// ui/auth/VerifyEmailScreen.kt
package com.ailingo.app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VerifyEmailScreen(
    onVerified: () -> Unit,
    onLogout: () -> Unit,
    vm: VerifyEmailViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value

    LaunchedEffect(Unit) {
        // Send the email once when user lands here (first time)
        vm.sendVerificationIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("Verify your email", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("We sent a verification link to:\n${ui.email}")
        Spacer(Modifier.height(24.dp))

        // Check button
        Button(
            onClick = { vm.checkVerified(onVerified) },
            enabled = !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ui.isLoading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(12.dp))
            }
            Text("I verified my email")
        }

        Spacer(Modifier.height(12.dp))

        // Resend cooldown
        Button(
            onClick = { vm.resend() },
            enabled = ui.canResend && !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (ui.canResend) "Resend verification email" else "Resend available in ${ui.resendSeconds}s")
        }

        if (ui.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(ui.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onLogout) { Text("Use different email (Log out)") }
    }
}

// ----- VM + UI state -----

data class VerifyUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val canResend: Boolean = true,
    val resendSeconds: Int = 0,
    val error: String? = null
)
