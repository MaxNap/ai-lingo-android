package com.ailingo.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ailingo.app.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape


private val BrandBlue = Color(0xFF1AB8E2)
private val BrandPurple = Color(0xFFCB39C3)
private val LinkColor = Color(0xFFEDF4FF)

@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String = "Your Name",
    userEmail: String = "your.email@example.com"
) {
    val gradientBrush = remember {
        Brush.sweepGradient(
            listOf(
                BrandBlue,
                BrandPurple,
                BrandBlue
            )
        )
    }
    val borderWidth = 4.dp

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        com.ailingo.app.ui.components.AlertDialog(onDismiss = { showDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()   // avoid bottom system bar overlap
            .imePadding()              // avoid keyboard overlap
            .padding(bottom = 96.dp),  // ensure Logout isn't cramped under bottom bar
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // --- User Info Section ---
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .border(BorderStroke(borderWidth, gradientBrush), CircleShape)
                .padding(borderWidth)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))

        // --- Account Section ---
        ProfileSection(title = "Account") {
            ProfileButton(text = "Edit Password", onClick = { showDialog = true })
            ProfileButton(text = "Notifications", onClick = { showDialog = true })
            ProfileButton(text = "Settings", onClick = { showDialog = true })
            ProfileButton(text = "Support", onClick = { showDialog = true })
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- About Section ---
        ProfileSection(title = "About") {
            ProfileButton(text = "Privacy Policy", onClick = { showDialog = true })
            ProfileButton(text = "Terms of Service", onClick = { showDialog = true })
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Log Out Button ---
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("welcome") {
                    popUpTo(0) // clear backstack
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPurple,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Log Out",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * A composable for creating a titled section with content.
 */
@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileButton(
    text: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp), // flat edges for unified card look
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1AB8E2), // brand blue
                contentColor = Color(0xFFEDF4FF)   // link text
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = text, fontSize = 16.sp, color = Color(0xFFEDF4FF))
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFEDF4FF)
                )
            }
        }

        if (showDivider) {
            Divider(
                color = Color.White.copy(alpha = 0.4f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    val navController = rememberNavController()
    ProfileScreen(
        navController = navController,
        userName = "AI Lingo User",
        userEmail = "lingo.user@email.com"
    )
}
