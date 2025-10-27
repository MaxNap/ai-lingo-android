package com.ailingo.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailingo.app.R

private val Purple = Color(0xFFCB39C3)
private val Blue = Color(0xFF1AB8E2)

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "AI Lingo Logo",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "AI Lingo",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Purple
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Log In button (solid purple)
        Button(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                contentColor = Color.White
            )
        ) {
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up button (outlined)
        OutlinedButton(
            onClick = onSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(
                    width = 2.dp,
                    color = Purple,
                    shape = RoundedCornerShape(30.dp)
                ),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Purple
            )
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
