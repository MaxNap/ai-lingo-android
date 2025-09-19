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
import com.ailingo.app.ui.screens.HomeScreen
import com.google.firebase.auth.FirebaseAuth

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
        // Splash: decides where to go based on current user
        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val isSignedIn = FirebaseAuth.getInstance().currentUser != null
                nav.navigate(if (isSignedIn) Routes.Home else Routes.SignIn) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        }

        composable(Routes.SignIn) {
            SignInScreen(
                onSignedIn = {
                    nav.navigate(Routes.Home) { popUpTo(0) }
                },
                onGoToSignUp = {
                    nav.navigate(Routes.SignUp)
                }
            )
        }

        composable(Routes.SignUp) {
            SignUpScreen(
                onSignedUp = {
                    nav.navigate(Routes.Home) { popUpTo(0) }
                },
                onGoToSignIn = {
                    nav.popBackStack() // go back to SignIn
                }
            )
        }

        composable(Routes.Home) {
            // Your existing Home screen. Add a sign-out button somewhere in Home to return to SignIn.
            HomeScreen()
        }
    }
}
