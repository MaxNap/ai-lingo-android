package com.ailingo.app

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ailingo.app.lesson.LessonOneScreen
import com.ailingo.app.lesson.LessonTwoScreen
import com.ailingo.app.ui.auth.SignInScreen
import com.ailingo.app.ui.auth.SignUpScreen
import com.ailingo.app.ui.auth.WelcomeScreen
import com.ailingo.app.ui.components.BottomNavBar
import com.ailingo.app.ui.screens.HomeScreen
import com.ailingo.app.ui.screens.LearnScreen
import com.ailingo.app.ui.screens.ProfileScreen
import com.ailingo.app.ui.screens.StudioScreen
import com.ailingo.app.ui.theme.AILingoTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AILingoTheme {
                val navController = rememberNavController()

                // Define bottom tabs once
                val tabs = listOf(
                    TabDest(Routes.Home,   "Home"),
                    TabDest(Routes.Learn,  "Learn"),
                    TabDest(Routes.Studio, "Studio"),
                    TabDest(Routes.ProfileSplash, "Profile"),
                )

                // Observe current route
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: Routes.Splash

                // Bottom bar should be hidden on Splash/Auth/Lessons
                val hideBottomBar =
                    currentRoute == Routes.Splash ||
                            currentRoute == Routes.Welcome ||
                            currentRoute == Routes.SignIn ||
                            currentRoute == Routes.SignUp ||
                            currentRoute.startsWith("lesson/")

                // >>> FIX: keep highlight correct when on profile_splash or profilescreen
                val selectedIndex = when {
                    currentRoute == Routes.Home   -> 0
                    currentRoute == Routes.Learn  -> 1
                    currentRoute == Routes.Studio -> 2
                    currentRoute == Routes.ProfileSplash || currentRoute == Routes.ProfileScreen -> 3
                    else -> 0
                }
                // <<< FIX

                Scaffold(
                    bottomBar = {
                        if (!hideBottomBar) {
                            BottomNavBar(
                                selectedTab = selectedIndex,
                                onTabSelected = { index ->
                                    val dest = tabs[index].route
                                    navController.navigate(dest) {
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
                        startDestination = Routes.Splash,
                        modifier = Modifier
                            .padding(inner)
                            .fillMaxSize()
                    ) {
                        // --- Splash gate: decide start based on auth ---
                        composable(Routes.Splash) {
                            LaunchedEffect(Unit) {
                                val isSignedIn = FirebaseAuth.getInstance().currentUser != null
                                navController.navigate(if (isSignedIn) Routes.Home else Routes.Welcome) {
                                    popUpTo(Routes.Splash) { inclusive = true }
                                }
                            }
                        }

                        // Welcome
                        composable(Routes.Welcome) {
                            WelcomeScreen(
                                onSignIn = { navController.navigate(Routes.SignIn) },
                                onSignUp = { navController.navigate(Routes.SignUp) }
                            )
                        }

                        // Auth
                        composable(Routes.SignIn) {
                            SignInScreen(
                                onSignedIn = {
                                    navController.navigate(Routes.Home) {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                },
                                onGoToSignUp = { navController.navigate(Routes.SignUp) }
                            )
                        }
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

                        // Tabs
                        composable(Routes.Home)   { HomeScreen(navController) }
                        composable(Routes.Learn)  { LearnScreen(navController) }
                        composable(Routes.Studio) { StudioScreen() }
                        composable(Routes.ProfileSplash){ ProfileSplashScreen(navController = navController) }
                        composable(Routes.ProfileScreen) { ProfileScreen(navController = navController) }

                        // Lessons
                        composable("lesson/1/1") {
                            LessonOneScreen(
                                onLessonComplete = { navController.popBackStack() },
                                onBackFromLesson = { navController.popBackStack() }
                            )
                        }
                        composable("lesson/1/2") {
                            LessonTwoScreen(
                                onLessonComplete = { navController.popBackStack() },
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

private object Routes {
    const val Splash  = "splash"
    const val Welcome = "welcome"
    const val SignIn  = "auth/signin"
    const val SignUp  = "auth/signup"
    const val Home    = "home"
    const val Learn   = "learn"
    const val Studio  = "studio"

    // Profile Page
    const val ProfileSplash = "profile_splash"
    const val ProfileScreen = "profilescreen"
}

@Composable
fun ProfileSplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val translationY = remember { Animatable(0f) }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 500,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        delay(3000L)
        // >>> FIX: use route constants so names match
        navController.navigate(Routes.ProfileScreen) {
            popUpTo(Routes.ProfileSplash) { inclusive = true }
        }
        // <<< FIX
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Logo",
            modifier = Modifier.fillMaxSize(scale.value)
        )
    }
}
