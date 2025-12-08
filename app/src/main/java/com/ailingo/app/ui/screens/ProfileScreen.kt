package com.ailingo.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ailingo.app.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText

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
            listOf(BrandBlue, BrandPurple, BrandBlue)
        )
    }
    val borderWidth = 4.dp

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeSection by remember { mutableStateOf<Section?>(null) }
    var deleteAccountPassword by remember { mutableStateOf("") }

    // ---------- Dialog Section ----------
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
                    Section.EditPassword -> Text("Enter a new password or follow instructions to change it.")
                    Section.Notifications -> Text("Manage your notification preferences.")

                    Section.Settings -> {
                        Column {
                            Text("Confirm password to delete your account")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = deleteAccountPassword,
                                onValueChange = { deleteAccountPassword = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Section.Support -> Text("Contact support or view FAQs.")
                    Section.PrivacyPolicy -> Text("View our privacy policy.")
                    Section.TermsOfService -> Text("Read the terms of service.")
                }
            },
            confirmText = when (section) {
                Section.EditPassword -> "Confirm Change"
                Section.Notifications -> "Save Preference"
                Section.Settings -> "Delete Account"
                Section.Support -> "support@ailingo.com"
                Section.PrivacyPolicy -> "Open"
                Section.TermsOfService -> "Open"
            },
            dismissText = "Cancel",
            onConfirm = {
                when (section) {
                    Section.Settings -> {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val email = currentUser?.email

                        if (currentUser != null && email != null && deleteAccountPassword.isNotBlank()) {
                            val credential = EmailAuthProvider.getCredential(email, deleteAccountPassword)

                            currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    currentUser.delete().addOnCompleteListener { deleteTask ->
                                        if (deleteTask.isSuccessful) {
                                            FirebaseAuth.getInstance().signOut()
                                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                            navController.navigate("welcome") { popUpTo(0) }
                                        } else Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show()
                                    }
                                } else Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
                            }
                        } else Toast.makeText(context, "Password required", Toast.LENGTH_SHORT).show()
                    }

                    Section.Support -> {
                        val email = "support@ailingo.com"
                        clipboardManager.setText(AnnotatedString(email))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }

                    Section.PrivacyPolicy -> {
                        val url = "https://www.freeprivacypolicy.com/live/13e82723-8e50-48f0-b47f-ee2c4e06e633"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }

                    else -> {}
                }
                activeSection = null
            },
            onDismiss = { activeSection = null }
        )
    }

    // ---------- MAIN UI ----------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .border(BorderStroke(borderWidth, gradientBrush), CircleShape)
                .padding(borderWidth)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user?.displayName ?: userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = user?.email ?: userEmail, color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        // ACCOUNT
        ProfileSection("Account") {
            ProfileButton("Edit Password") { activeSection = Section.EditPassword }
            ProfileButton("Notifications") { activeSection = Section.Notifications }
            ProfileButton("Settings") { activeSection = Section.Settings }
            ProfileButton("Support") { activeSection = Section.Support }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // ABOUT
        ProfileSection("About") {
            ProfileButton("Privacy Policy") { activeSection = Section.PrivacyPolicy }
            ProfileButton("Terms of Service") { activeSection = Section.TermsOfService }
        }


        Spacer(modifier = Modifier.height(40.dp))

        // LOG OUT
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("welcome") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPurple, contentColor = Color.White)
        ) {
            Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .border(1.dp, Color(0xFFE0E0E0), MaterialTheme.shapes.medium)
            ) { content() }
        }
    }
}

@Composable
private fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = LinkColor),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    val nav = rememberNavController()
    ProfileScreen(navController = nav)
}
