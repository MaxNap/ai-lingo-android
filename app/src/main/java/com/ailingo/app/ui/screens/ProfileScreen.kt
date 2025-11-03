package com.ailingo.app.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.EmailAuthProvider
import android.net.Uri


private val BrandBlue = Color(0xFF1AB8E2)
private val BrandPurple = Color(0xFFCB39C3)
private val LinkColor = Color(0xFFEDF4FF)

private sealed class Section {
    object EditPassword : Section()
    object Notifications : Section()
    object Settings : Section()
    object Support : Section()
    object PrivacyPolicy : Section()
    object TermsOfService : Section()
}


@Composable
fun ProfileScreen(
    navController: NavController,
    userName: String = "Your Name",
    userEmail: String = "your.email@example.com"
) {
    val user = FirebaseAuth.getInstance().currentUser

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

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeSection by remember { mutableStateOf<Section?>(null) }
    var deleteAccount by remember { mutableStateOf("") }

    activeSection?.let { section ->
        com.ailingo.app.ui.components.AlertDialog(
            title = when (section) {
                Section.EditPassword -> "Edit Password"
                Section.Notifications -> "Notifications"
                Section.Settings -> "Settings"
                Section.Support -> "Support"
                Section.PrivacyPolicy -> "Privacy Policy"
                Section.TermsOfService -> "Terms of Service"
            },
            body = {
                when (section) {
                    /* Account Section */
                    /* --------------- */
                    Section.EditPassword -> Text("Enter a new password or follow instructions to change it.")
                    // ↑ Text: input x3 (old, new, new), confirm → Change password
                    // * Very complex setup —will need more time to implement
                    Section.Notifications -> Text("Manage your notification preferences.")
                    // ↑ Radio: daily notification (on/off), confirm → Save preference + turn on/off notifications
                    // * Requires a number of background functionality to be implemented — Saved for later... (includes time picker and data storage)
                    Section.Settings -> {
                        var password by remember { mutableStateOf("") }
                        Column {
                            Text("Confirm Password to delete your account")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = deleteAccount,
                                onValueChange = { deleteAccount = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    // ↑ Delete Account: Button → Warning → Confirmation → Password input → Delete account
                    //
                    Section.Support -> Text("Contact support or view FAQs.")
                    // ↑ Link: Support Email

                    /* About Section */
                    /* ------------- */
                    Section.PrivacyPolicy -> Text("View our privacy policy.")
                    // ↑ Link: Privacy Policy
                    Section.TermsOfService -> Text("Read the terms of service.")
                    // ↑ Link: Terms of Service
                }
            },
            confirmText = when (section) {
                Section.EditPassword -> "Confirm Change"

                Section.Notifications -> "Save Preference"

                Section.Settings -> "Delete Account"

                Section.Support -> "support@ailingo.com"

                Section.PrivacyPolicy -> "Privacy Policy"

                Section.TermsOfService -> "Terms of Service"

            },
            dismissText = "Cancel",
            onConfirm = {
                // perform section-specific action
                when (section) {
                    Section.EditPassword -> {
                        // ↑ Verify correct input → Confirm Password Change
                    }
                    Section.Notifications -> {
                        // ↑ Save Notification Preference
                    }
                    Section.Settings -> {
                        val user = FirebaseAuth.getInstance().currentUser
                        val email = user?.email

                        if (user != null && email != null && deleteAccount.isNotBlank()) {
                            val credential = EmailAuthProvider.getCredential(email, deleteAccount)

                            user.reauthenticate(credential).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    user.delete().addOnCompleteListener { deleteTask ->
                                        if (deleteTask.isSuccessful) {
                                            FirebaseAuth.getInstance().signOut()
                                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                            navController.navigate("welcome") {
                                                popUpTo(0) // clear backstack
                                            }
                                        } else {
                                            Toast.makeText(context, "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Authentication failed. Please check your password.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Password cannot be empty.", Toast.LENGTH_SHORT).show()
                        }
                        // ↑ Verify correct input → Delete Account → Return to home page
                    }
                    Section.Support -> {
                        val supportEmail = "support@ailingo.com"
                        clipboardManager.setText(AnnotatedString(supportEmail))
                        Toast.makeText(context, "Email copied to clipboard", Toast.LENGTH_SHORT).show()
                        // ↑ Link: Support Email (placeholder email)
                    }
                    Section.PrivacyPolicy -> {
                        val privacyPolicyUrl = "https://www.freeprivacypolicy.com/live/13e82723-8e50-48f0-b47f-ee2c4e06e633"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                        context.startActivity(intent)
                        // ↑ Link: [https://www.freeprivacypolicy.com/live/13e82723-8e50-48f0-b47f-ee2c4e06e633]
                    }
                    Section.TermsOfService -> {
                        // ↑ Link: *Costs money —so it is not created yet.
                    }
                }
                activeSection = null
            },
            onDismiss = { activeSection = null }
        )
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
            text = user?.displayName ?: userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user?.email ?: userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))

        // --- Account Section ---
        ProfileSection(title = "Account") {
            ProfileButton(text = "Edit Password", onClick = { activeSection = Section.EditPassword })
            ProfileButton(text = "Notifications", onClick = { activeSection = Section.Notifications })
            ProfileButton(text = "Settings", onClick = { activeSection = Section.Settings })
            ProfileButton(text = "Support", onClick = { activeSection = Section.Support })
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- About Section ---
        ProfileSection(title = "About") {
            ProfileButton(text = "Privacy Policy", onClick = { activeSection = Section.PrivacyPolicy })
            ProfileButton(text = "Terms of Service", onClick = { activeSection = Section.TermsOfService })
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
