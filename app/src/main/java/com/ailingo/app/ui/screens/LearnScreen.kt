package com.ailingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LearnScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Learn Screen")

        Spacer(modifier = Modifier.height(16.dp))

        // Add Lesson 1 button
        Button(
            onClick = { navController.navigate("lesson/1/1") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Lesson 1: What is a Prompt?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Add Lesson two button
        Button(
            onClick = { navController.navigate("lesson/1/2") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Lesson 2: Saying Hello")
        }
    }
}