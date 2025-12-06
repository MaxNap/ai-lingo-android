package com.ailingo.app

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.ailingo.app.ui.screens.LessonOverviewScreen
import com.ailingo.app.ui.theme.AILingoTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ No emulator setup here anymore – app talks to real Firebase project

        setContent {
            AILingoTheme {
                val navController = rememberNavController()

                // Bottom tabs
                val tabs = listOf(
                    TabDest(Routes.Home,   "Home"),
                    TabDest(Routes.Learn,  "Learn"),
                    TabDest(Routes.Studio, "Studio"),
                    TabDest(Routes.ProfileScreen, "Profile"),
                )

                // Observe current route
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: Routes.Splash

                // Hide bottom bar on these routes
                val hideBottomBar =
                    currentRoute == Routes.Splash ||
                            currentRoute == Routes.Welcome ||
                            currentRoute == Routes.VerifyEmail ||
                            currentRoute == Routes.SignIn ||
                            currentRoute == Routes.SignUp ||
                            currentRoute == Routes.ProfileSplash ||
                            currentRoute.startsWith("lesson/")

                // Keep highlight correct for profile splash/profile screen
                val selectedIndex = when (currentRoute) {
                    Routes.Home   -> 0
                    Routes.Learn  -> 1
                    Routes.Studio -> 2
                    Routes.ProfileScreen -> 3
                    else -> 0
                }

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
                        // Splash: decide where to go
                        composable(Routes.Splash) {
                            LaunchedEffect(Unit) {
                                val user = FirebaseAuth.getInstance().currentUser
                                when {
                                    user == null -> {
                                        navController.navigate(Routes.Welcome) {
                                            popUpTo(Routes.Splash) { inclusive = true }
                                        }
                                    }
                                    user.isEmailVerified -> {
                                        navController.navigate(Routes.Home) {
                                            popUpTo(Routes.Splash) { inclusive = true }
                                        }
                                    }
                                    else -> {
                                        navController.navigate(Routes.VerifyEmail) {
                                            popUpTo(Routes.Splash) { inclusive = true }
                                        }
                                    }
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
                                    val user = FirebaseAuth.getInstance().currentUser
                                    navController.navigate(
                                        if (user?.isEmailVerified == true) Routes.ProfileSplash else Routes.VerifyEmail
                                    ) {
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
                                    // After sign-up we sent a verification email; now gate to Verify screen
                                    navController.navigate(Routes.VerifyEmail) {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                },
                                onGoToSignIn = { navController.popBackStack() }
                            )
                        }

                        // Verify Email
                        composable(Routes.VerifyEmail) {
                            VerifyEmailScreen(
                                onContinueIfVerified = {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user?.isEmailVerified == true) {
                                        navController.navigate(Routes.ProfileSplash) {
                                            popUpTo(0); launchSingleTop = true
                                        }
                                    }
                                },
                                onSignOut = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(Routes.Welcome) { popUpTo(0) }
                                }
                            )
                        }

                        // Tabs
                        composable(Routes.Home)   { HomeScreen(navController) }
                        composable(Routes.Learn)  { LearnScreen(navController) }
                        composable(Routes.Studio) { StudioScreen() }
                        composable(Routes.ProfileSplash){ ProfileSplashScreen(navController = navController) }
                        composable(Routes.ProfileScreen) { ProfileScreen(navController = navController) }

                        // Overview for a unit (currently unit 1)
                        composable(
                            route = "${com.ailingo.app.ui.navigation.Routes.LessonOverviewBase}/{unitId}"
                        ) { backStackEntry ->
                            val unitId = backStackEntry.arguments?.getString("unitId") ?: "1"

                            LessonOverviewScreen(
                                navController = navController,
                                lessonId = unitId
                            )
                        }

                        // Lessons (dynamic: lesson/{unitId}/{lessonId})
                        composable(
                            route = "lesson/{unitId}/{lessonId}"
                        ) { backStackEntry ->
                            val unitId = backStackEntry.arguments?.getString("unitId") ?: "1"
                            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: "1"

                            when (lessonId) {
                                "1" -> LessonOneScreen(
                                    onLessonComplete = { navController.popBackStack() },
                                    onBackFromLesson = { navController.popBackStack() }
                                )
                                "2" -> LessonTwoScreen(
                                    onLessonComplete = { navController.popBackStack() },
                                    onBackFromLesson = { navController.popBackStack() }
                                )
                                // later: add "3", "4" here when lessons exist
                            }
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
    const val VerifyEmail = "auth/verify"
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
    val scale = remember { Animatable(0.8f) }     // start slightly smaller
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,                    // full size
            animationSpec = tween(
                durationMillis = 500,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        delay(1200L)
        navController.navigate(Routes.Home) {
            popUpTo(Routes.ProfileSplash) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ailingo_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.5f)              // baseline size (50% width)
                .scale(scale.value)              // apply the animation
        )
    }
}

/* ---------- Lightweight Verify Email Screen ---------- */

@Composable
private fun VerifyEmailScreen(
    onContinueIfVerified: () -> Unit,
    onSignOut: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    var info by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Scaffold { pad ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(pad)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Verify your email",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.padding(vertical = 8.dp))
            Text(
                "We’ve sent a verification link to ${auth.currentUser?.email ?: ""}. " +
                        "Open the link in your email, then tap the button below.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.padding(vertical = 24.dp))

            Button(
                onClick = {
                    loading = true
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            loading = false
                            info = if (it.isSuccessful) {
                                "Verification email sent. Please check your inbox."
                            } else {
                                it.exception?.localizedMessage ?: "Failed to send email."
                            }
                        }
                },
                enabled = !loading
            ) { Text("Resend email") }

            Spacer(Modifier.padding(vertical = 12.dp))

            Button(
                onClick = {
                    loading = true
                    auth.currentUser?.reload()
                        ?.addOnCompleteListener {
                            loading = false
                            onContinueIfVerified()
                            if (auth.currentUser?.isEmailVerified != true) {
                                info = "Not verified yet. Please open the link and try again."
                            }
                        }
                },
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text("I verified – Continue") }

            Spacer(Modifier.padding(vertical = 12.dp))

            TextButton(onClick = onSignOut, enabled = !loading) { Text("Sign out") }

            if (info != null) {
                Spacer(Modifier.padding(vertical = 8.dp))
                Text(info!!, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
