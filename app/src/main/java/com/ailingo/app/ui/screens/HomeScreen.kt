package com.ailingo.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ailingo.app.R
import com.ailingo.app.lesson.data.ProgressRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val BrandBlue = Color(0xFF1AB8E2)
private val BrandPurple = Color(0xFFCB39C3)

data class UserStatsUi(
    val xp: Int = 0,
    val streak: Int = 0,
    val lessonsCompleted: Int = 0
)

@Composable
fun HomeScreen(
    navController: NavController,
    userName: String = "Your Name"
) {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    val progressRepo = remember { ProgressRepository() }
    val scope = rememberCoroutineScope()

    // Load stats from Firestore once per uid
    val stats by produceState(
        initialValue = UserStatsUi(),
        key1 = uid
    ) {
        if (uid == null) {
            value = UserStatsUi()
        } else {
            val db = FirebaseFirestore.getInstance()
            val snap = db.collection("users").document(uid).get().await()

            if (!snap.exists()) {
                value = UserStatsUi()
            } else {
                val xp = (snap.getLong("xpTotal") ?: 0L).toInt()
                val streak = (snap.getLong("streak") ?: 0L).toInt()
                val lessonsCompleted =
                    (snap.getLong("lessonsCompletedCount") ?: 0L).toInt()

                value = UserStatsUi(
                    xp = xp,
                    streak = streak,
                    lessonsCompleted = lessonsCompleted
                )
            }
        }
    }

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
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Avatar + name
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
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

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome back to AI-Lingo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats section
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Your progress",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "XP",
                        value = stats.xp.toString(),
                        description = "Total earned",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Streak",
                        value = stats.streak.toString(),
                        description = "Day streak",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Lessons",
                        value = stats.lessonsCompleted.toString(),
                        description = "Completed",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // DEBUG button to test progress + Cloud Function
        var debugMessage by remember { mutableStateOf<String?>(null) }

        Button(
            onClick = {
                debugMessage = "Sending progress…"
                scope.launch {
                    try {
                        progressRepo.markLessonCompleted(
                            courseId = "courseA",
                            lessonId = "lesson1",
                            xpReward = 10
                        )
                        debugMessage = "OK: progress written. Check Firestore & Cloud Function."
                    } catch (t: Throwable) {
                        debugMessage = "Error: ${t.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DEBUG: Mark Lesson 1 Completed")
        }

        if (debugMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = debugMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F7FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
