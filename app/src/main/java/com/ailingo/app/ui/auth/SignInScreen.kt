package com.ailingo.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onGoToSignUp: () -> Unit,
    vm: SignInViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value
    val emailValid = remember(ui.email) { Patterns.EMAIL_ADDRESS.matcher(ui.email).matches() }
    val passwordValid = remember(ui.password) { ui.password.length >= 8 }
    val formValid = emailValid && passwordValid && !ui.isLoading

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome back", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Sign in to continue", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::updateEmail,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = ui.email.isNotBlank() && !emailValid,
                supportingText = {
                    if (ui.email.isNotBlank() && !emailValid)
                        Text("Enter a valid email")
                }
            )

            Spacer(Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password (min 8 chars)") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "HIDE" else "SHOW")
                    }
                },
                isError = ui.password.isNotBlank() && !passwordValid,
                supportingText = {
                    if (ui.password.isNotBlank() && !passwordValid)
                        Text("Password must be at least 8 characters")
                }
            )

            if (ui.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(ui.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.signIn(onSuccess = onSignedIn) },
                enabled = formValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text("Sign In")
            }

            TextButton(
                onClick = {
                    vm.resetPasswordIfEmailValid()
                },
                enabled = !ui.isLoading
            ) { Text("Forgot password?") }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = onGoToSignUp,
                enabled = !ui.isLoading
            ) {
                Text("No account? Create one")
            }
        }
    }
}
