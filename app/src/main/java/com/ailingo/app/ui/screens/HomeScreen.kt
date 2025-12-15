package com.ailingo.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private val BrandBlue = Color(0xFF1AB8E2)
private val BrandPurple = Color(0xFFCB39C3)

data class UserStatsUi(
    val xp: Int = 0,
    val streak: Int = 0,
    val lessonsCompleted: Int = 0
)

private data class LevelInfo(
    val level: Int,
    val label: String,
    val minXp: Int,
    val maxXp: Int? // null = no upper limit (max level in preview)
)

/**
 * Simple level system:
 * - Level 0:   0–9 XP        (New Learner)
 * - Level 1:  10–29 XP      (Getting Started)
 * - Level 2:  30–59 XP      (Prompt Explorer)
 * - Level 3:  60–99 XP      (AI Buddy)
 * - Level 4+: 100+ XP       (Prompt Pro)
 */
private fun getLevelInfo(xp: Int): LevelInfo {
    return when {
        xp < 10 -> LevelInfo(level = 0, label = "New Learner", minXp = 0, maxXp = 10)
        xp < 30 -> LevelInfo(level = 1, label = "Getting Started", minXp = 10, maxXp = 30)
        xp < 60 -> LevelInfo(level = 2, label = "Prompt Explorer", minXp = 30, maxXp = 60)
        xp < 100 -> LevelInfo(level = 3, label = "AI Buddy", minXp = 60, maxXp = 100)
        else -> LevelInfo(level = 4, label = "Prompt Pro", minXp = 100, maxXp = null)
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    userName: String = "Your Name"
) {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

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
                val lessonsCompleted = (snap.getLong("lessonsCompletedCount") ?: 0L).toInt()

                value = UserStatsUi(
                    xp = xp,
                    streak = streak,
                    lessonsCompleted = lessonsCompleted
                )
            }
        }
    }

    val gradientBrush = remember {
        Brush.sweepGradient(listOf(BrandBlue, BrandPurple, BrandBlue))
    }
    val borderWidth = 4.dp

    val levelInfo = remember(stats.xp) { getLevelInfo(stats.xp) }

    // Progress between current level min and next level max
    val levelProgress: Float = remember(stats.xp, levelInfo) {
        val maxXp = levelInfo.maxXp
        if (maxXp == null) {
            1f // Max level in this preview
        } else {
            val span = (maxXp - levelInfo.minXp).coerceAtLeast(1)
            val gained = (stats.xp - levelInfo.minXp).coerceAtLeast(0)
            (gained.toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }
    }

    // Daily goal (simple v1: use total XP capped to goal)
    val dailyGoalXp = 10
    val xpToday = remember(stats.xp) { stats.xp.coerceIn(0, dailyGoalXp) }
    val dailyProgress = remember(xpToday) { (xpToday.toFloat() / dailyGoalXp).coerceIn(0f, 1f) }
    val goalDone = xpToday >= dailyGoalXp

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
            text = "Welcome back to AILingo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats section
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            color = Color(0xFFEDF4FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Your progress",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
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

        Spacer(modifier = Modifier.height(24.dp))

        // Level section
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            color = Color(0xFFEDF4FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Your level",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Level ${levelInfo.level}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = levelInfo.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${stats.xp} XP",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = BrandBlue
                )

                Spacer(Modifier.height(8.dp))

                val nextText = if (levelInfo.maxXp == null) {
                    "You’ve reached the top level in this preview."
                } else {
                    val xpToNext = (levelInfo.maxXp - stats.xp).coerceAtLeast(0)
                    "$xpToNext XP to reach level ${levelInfo.level + 1}"
                }

                Text(
                    text = nextText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily goal section (Option B)
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
            color = if (goalDone) Color(0xFFE9F8EF) else Color(0xFFEDF4FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Daily goal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Daily goal: $dailyGoalXp XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { dailyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = if (goalDone) Color(0xFF2DBF6C) else BrandBlue
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "You earned $xpToday/$dailyGoalXp XP today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (goalDone) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Goal completed",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF2DBF6C)
                    )
                }
            }
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
