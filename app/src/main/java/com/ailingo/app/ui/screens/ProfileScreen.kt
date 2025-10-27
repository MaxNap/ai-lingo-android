package com.ailingo.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import com.ailingo.app.R


@Composable
fun ProfileScreen(
    // You can pass the user's details as parameters from your ViewModel or parent Composable
    userName: String = "Your Name",
    userEmail: String = "your.email@example.com"
) {
    val gradientBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF1AB8E2),
                Color(0xFFCB39C3),
                Color(0xFF1AB8E2)
            )
        )
    }
    val borderWidth = 4.dp

    // AlertDialog Overlay
    var showDialog by remember { mutableStateOf( false) }
    if (showDialog){
        com.ailingo.app.ui.components.AlertDialog(
            onDismiss = { showDialog = false }
        )
    }

    // Added a Column with verticalScroll to handle different screen sizes
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Makes the page scrollable
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp)) // Pushes content down from the top

        // --- User Info Section ---
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .border(
                    BorderStroke(borderWidth, gradientBrush),
                    CircleShape
                )
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
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * A styled button for the profile page lists.
 */
@Composable
private fun ProfileButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = null, // Handled by the parent Card
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f)) // Pushes the icon to the end
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
    HorizontalDivider(thickness = 5.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
}


@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {

    ProfileScreen(
        userName = "AI Lingo User",
        userEmail = "lingo.user@email.com"

    )
}
