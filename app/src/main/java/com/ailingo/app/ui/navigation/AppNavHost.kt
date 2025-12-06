package com.ailingo.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ailingo.app.ui.auth.SignInScreen
import com.ailingo.app.ui.auth.SignUpScreen
import com.ailingo.app.ui.auth.WelcomeScreen
import com.ailingo.app.ui.screens.HomeScreen
import com.ailingo.app.ui.screens.LessonOverviewScreen
import com.google.firebase.auth.FirebaseAuth
import com.ailingo.app.lesson.LessonOneScreen
import com.ailingo.app.ui.screens.LearnScreen



@Composable
fun AppNavHost(
    nav: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = nav,
        startDestination = Routes.Splash,
        modifier = modifier
    ) {

        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val isSignedIn = FirebaseAuth.getInstance().currentUser != null
                nav.navigate(if (isSignedIn) Routes.Home else Routes.Welcome) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        }

        // --- Auth flow ---
        composable(Routes.Welcome) {
            WelcomeScreen(
                onSignIn = { nav.navigate(Routes.SignIn) },
                onSignUp = { nav.navigate(Routes.SignUp) }
            )
        }

        composable(Routes.SignIn) {
            SignInScreen(
                onSignedIn = { nav.navigate(Routes.Home) { popUpTo(0) } },
                onGoToSignUp = { nav.navigate(Routes.SignUp) }
            )
        }

        composable(Routes.SignUp) {
            SignUpScreen(
                onSignedUp = { nav.navigate(Routes.Home) { popUpTo(0) } },
                onGoToSignIn = { nav.popBackStack() }
            )
        }

        // --- Main/Home with bottom nav ---
        composable(Routes.Home) {
            HomeScreen(navController = nav)
        }

        // --- Learn list ---
        composable(Routes.Learn) {
            LearnScreen(navController = nav)
        }


        // --- Lesson Overview (unit screen) ---
        // Navigate with: nav.navigate(Routes.lessonOverview("1"))
        composable("${Routes.LessonOverviewBase}/{unitId}") { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId") ?: "1"
            LessonOverviewScreen(
                navController = nav,
                lessonId = unitId
            )
        }



        // --- Lesson content ---
        // Navigate with: nav.navigate(Routes.lesson("1", "1"))
        composable("lesson/{unitId}/{lessonId}") { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId") ?: "1"
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: "1"

            when (lessonId) {
                "1" -> LessonOneScreen(
                    onLessonComplete = { nav.popBackStack() },
                    onBackFromLesson = { nav.popBackStack() }
                )
                // later:
                // "2" -> LessonTwoScreen(...)
            }
        }

    }
}
