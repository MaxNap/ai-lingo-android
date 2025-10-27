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

        composable(Routes.Splash) {
            LaunchedEffect(Unit) {
                val isSignedIn = FirebaseAuth.getInstance().currentUser != null

                nav.navigate(if (isSignedIn) Routes.Home else Routes.Welcome) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        }


        composable(Routes.Welcome) {
            WelcomeScreen(
                onSignIn = { nav.navigate(Routes.SignIn) },
                onSignUp = { nav.navigate(Routes.SignUp) }
            )
        }

        // --- Sign In ---
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

        // --- Sign Up ---
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

        // --- Home ---
        composable(Routes.Home) {
            HomeScreen(navController = nav)
        }
    }
}
