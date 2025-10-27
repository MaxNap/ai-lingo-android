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
fun SignInScreen(
    onSignedIn: () -> Unit,
    onGoToSignUp: () -> Unit,
    vm: SignInViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value
    val emailValid = remember(ui.email) { Patterns.EMAIL_ADDRESS.matcher(ui.email).matches() }
    val passwordValid = remember(ui.password) { ui.password.length >= 8 }
    val formValid = emailValid && passwordValid && !ui.isLoading

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Light header strip like your mock
            Surface(color = Blue.copy(alpha = 0.08f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Log In",
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
            // ---- EMAIL ----
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

            Spacer(Modifier.height(20.dp))

            // ---- PASSWORD ----
            Text("Password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::updatePassword,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your password") },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "HIDE" else "SHOW")
                    }
                },
                isError = ui.password.isNotBlank() && !passwordValid
            )

            if (ui.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(ui.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // ---- FORGOT PASSWORD ----
            Text(
                "Forgot Password?",
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = !ui.isLoading) {
                    vm.resetPasswordIfEmailValid()
                }
            )

            Spacer(Modifier.height(28.dp))

            // ---- CTA ----
            Button(
                onClick = { vm.signIn(onSuccess = onSignedIn) },
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
                Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(28.dp))

            // ---- FOOTER ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don’t have an account? ")
                Text(
                    "Sign Up",
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(enabled = !ui.isLoading) { onGoToSignUp() }
                )
            }
        }
    }
}
