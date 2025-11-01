package com.ailingo.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val Purple = Color(0xFFCB39C3)
private val Blue = Color(0xFF1AB8E2)

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onGoToSignIn: () -> Unit,
    vm: SignUpViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value

    // Local-only fields
    var confirm by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // Validation
    val emailValid = remember(ui.email) { Patterns.EMAIL_ADDRESS.matcher(ui.email).matches() }
    val lengthOK = ui.password.length >= 8
    val hasUpper = ui.password.any { it.isUpperCase() }
    val hasLower = ui.password.any { it.isLowerCase() }
    val hasDigitOrSymbol = ui.password.any { it.isDigit() || !it.isLetterOrDigit() }
    val passwordsMatch = ui.password.isNotEmpty() && ui.password == confirm
    val usernameValid = ui.username.trim().isNotEmpty()

    val formValid =
        usernameValid && emailValid && lengthOK && hasUpper && hasLower && hasDigitOrSymbol &&
                passwordsMatch && !ui.isLoading

    Scaffold(
        topBar = {
            Surface(color = Blue.copy(alpha = 0.08f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "New Account",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            Text("Username", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.username,
                onValueChange = vm::updateUsername,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Create your username") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = ui.username.isNotBlank() && !usernameValid
            )

            Spacer(Modifier.height(24.dp))

            // Email
            Text("Email", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::updateEmail,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = ui.email.isNotBlank() && !emailValid
            )

            Spacer(Modifier.height(24.dp))

            // Password
            Text("Password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your password") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    TextButton(onClick = { passVisible = !passVisible }) {
                        Text(if (passVisible) "HIDE" else "SHOW")
                    }
                },
                isError = ui.password.isNotBlank() && !(lengthOK && hasUpper && hasLower && hasDigitOrSymbol)
            )

            // Password rules
            Spacer(Modifier.height(16.dp))
            PasswordRule("At least 8 characters.", lengthOK)
            PasswordRule("At least one capital letter.", hasUpper)
            PasswordRule("At least one small letter.", hasLower)
            PasswordRule("At least one number or symbol.", hasDigitOrSymbol)

            Spacer(Modifier.height(24.dp))

            // Confirm Password
            Text("Confirm Password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Confirm your password") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    TextButton(onClick = { confirmVisible = !confirmVisible }) {
                        Text(if (confirmVisible) "HIDE" else "SHOW")
                    }
                },
                isError = confirm.isNotBlank() && !passwordsMatch
            )

            if (ui.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(ui.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(28.dp))

            // Create Account CTA
            Button(
                onClick = { vm.signUp(onSuccess = onSignedUp) },
                enabled = formValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White
                )
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = Color.White
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ")
                Text(
                    "Log in",
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(enabled = !ui.isLoading) { onGoToSignIn() }
                )
            }
        }
    }
}

@Composable
private fun PasswordRule(text: String, ok: Boolean) {
    val color = if (ok) Color(0xFF2E7D32) else Color.Black
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("✓  ", color = color)
        Text(text, color = color)
    }
}
// Some parts of this function were generated or inspired by ChatGPT (OpenAI).
