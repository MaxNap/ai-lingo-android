package com.ailingo.app

import android.R.attr.onClick
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                val context = LocalContext.current
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
                                    if (dest == Routes.Profile) {
                                        context.startActivity(Intent(context, ProfileScreen::class.java))
                                    } else {
                                    navController.navigate(dest) {
                                        // Keep a single instance of each tab and restore state
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
<<<<<<< Updated upstream
                        // Top-level tabs (kept in bottom nav backstack)
                        composable("home")   { HomeScreen() }
                        composable("learn")  { LearnScreen(navController) } // pass navController so it can navigate to lessons
                        composable("studio") { StudioScreen() }
                        composable("profile"){ ProfileScreen() }
=======
                        // --- Splash gate: decide start based on auth ---
                        composable(Routes.Splash) {
                            LaunchedEffect(Unit) {
                                val isSignedIn = FirebaseAuth.getInstance().currentUser != null
                                navController.navigate(if (isSignedIn) Routes.Home else Routes.SignIn) {
                                    popUpTo(Routes.Splash) { inclusive = true }
                                }
                            }
                        }

                        // --- Auth screens ---
                        composable(Routes.SignIn) {
                            SignInScreen(
                                onSignedIn = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(0)     // clear back stack
                                        launchSingleTop = true
                                    }
                                },
                                onGoToSignUp = { navController.navigate(Routes.SignUp) }
                            )
                        }


                        // Lesson detail routes (bottom bar hidden)

                        composable(Routes.SignUp) {
                            SignUpScreen(
                                onSignedUp = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                },
                                onGoToSignIn = { navController.popBackStack() }
                            )
                        }

                        // --- Top-level tabs (shown when authenticated) ---
                        composable(Routes.Home)   { HomeScreen() }
                        composable(Routes.Learn)  { LearnScreen(navController) }
                        composable(Routes.Studio) { StudioScreen() }
                        //composable(Routes.Profile){ ProfileScreen() }

                        // --- Lessons (hide bottom bar) ---
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
data class TabDest(val route: String, val title: String)
=======
// ** Commented out because it wouldn't run otherwise -- DON'T OVERLOOK
//data class TabDest(val route: String, val title: String)

// Route constants used by MainActivity & tabs.
// Keeping them here avoids "Unresolved reference 'Routes'" problems.
private object Routes {
    const val Splash  = "splash"
    const val SignIn  = "auth/signin"
    const val SignUp  = "auth/signup"
    const val Home    = "home"
    const val Learn   = "learn"
    const val Studio  = "studio"
    const val Profile = "profile"
}

data class TabDest(val route: String, val title: String)

>>>>>>> Stashed changes
