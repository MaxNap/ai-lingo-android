package com.ailingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ailingo.app.ui.components.BottomNavBar
import com.ailingo.app.ui.theme.AILingoTheme
import com.ailingo.app.ui.screens.HomeScreen
import com.ailingo.app.ui.screens.LearnScreen
import com.ailingo.app.ui.screens.StudioScreen
import com.ailingo.app.ui.screens.ProfileScreen
import com.ailingo.app.lesson.LessonOneScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AILingoTheme {
                val navController = rememberNavController()

                // Define bottom tabs once
                val tabs = listOf(
                    TabDest("home", "Home"),
                    TabDest("learn", "Learn"),
                    TabDest("studio", "Studio"),
                    TabDest("profile", "Profile"),
                )

                // Observe route to compute selected index & hide bar for lesson screens
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: tabs.first().route
                val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
                val hideBottomBar = currentRoute.startsWith("lesson/")

                Scaffold(
                    bottomBar = {
                        if (!hideBottomBar) {
                            BottomNavBar(
                                selectedTab = selectedIndex,
                                onTabSelected = { index ->
                                    val dest = tabs[index].route
                                    navController.navigate(dest) {
                                        // Keep a single instance of each tab and restore state
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { inner ->
                    NavHost(
                        navController = navController,
                        startDestination = tabs.first().route,
                        modifier = Modifier
                            .padding(inner)
                            .fillMaxSize()
                    ) {
                        // Top-level tabs (kept in bottom nav backstack)
                        composable("home")   { HomeScreen() }
                        composable("learn")  { LearnScreen(navController) } // pass navController so it can navigate to lessons
                        composable("studio") { StudioScreen() }
                        composable("profile"){ ProfileScreen() }

                        // Lesson detail route (bottom bar hidden)
                        composable("lesson/1/1") {
                            LessonOneScreen(
                                onLessonComplete = { navController.popBackStack() }, // return to Learn tab
                                onBackFromLesson = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TabDest(val route: String, val title: String)
