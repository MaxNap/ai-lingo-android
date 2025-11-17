package com.ailingo.app.ui.screens

import android.R.attr.text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
//
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ailingo.app.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.LinearProgressIndicator


private val BrandBlue = Color(0xFF1AB8E2)
private val BrandPurple = Color(0xFFCB39C3)
private val LinkColor = Color(0xFFEDF4FF)


// DAILY STREAK
// TODO: Create boundaries for date and month (ie: 1-31, 1-12)
// TODO: call calculateStreak()
var lastLoggedIn = 0
var dailyStreak = 0

fun getCurrentDate(): Int {
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
    val dateInt = dateFormat.format(Date()).toInt()
    return dateInt
}
fun getCurrentMonth(): Int {
    val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
    val monthInt = monthFormat.format(Date()).toInt()
    return monthInt
}
// Boundary set up goes here

fun streakAndCounterUp() {
    lastLoggedIn = getCurrentDate()
    dailyStreak++
}
fun streakAndCounterDown() {
    lastLoggedIn = getCurrentDate()
    dailyStreak = 0
}
// This is the calculateStreak needs to be called
fun calculateStreak() {
    if((lastLoggedIn - 1) == getCurrentDate()){
        streakAndCounterUp()
    } else {
        streakAndCounterDown()
    }
}

// EXPERIENCE
// TODO: Implement Level/Experience System
// TODO: Progress Bar
var experiencePoints = 0
var level = 1
var xpToNextLevel = 100



fun addExperience(points: Int) {
    experiencePoints += points

    if (experiencePoints >= xpToNextLevel) {
        level++
        experiencePoints -= xpToNextLevel

        if (level == 2) {
            xpToNextLevel = 200
        } else if (level == 3) {
            xpToNextLevel = 300
        } else if (level == 4) {
            xpToNextLevel = 400
        } else if (level == 5) {
            xpToNextLevel = 500
        }
    }
    // Progress Bar
}

@Composable
fun HomeScreen(
    navController: NavController,
    userName: String = "Your Name", ) {
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
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            // user?.dailyStreak ?: ↓
            text = "Daily Streak: ${dailyStreak}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "XP: $experiencePoints",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Level: $level",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

    }
}
