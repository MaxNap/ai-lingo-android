package com.ailingo.app.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val SignIn = "auth/signin"
    const val SignUp = "auth/signup"
    const val Home = "home"
}

@Composable
fun AuthNavHost(nav: NavHostController) {
    val start = if (FirebaseAuth.getInstance().currentUser == null) Routes.SignIn else Routes.Home

    NavHost(navController = nav, startDestination = start) {

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

        composable(Routes.Home) {
            // Your HomeScreen()
            // Include a sign-out option:
            // AuthRepository().signOut(); nav.navigate(Routes.SignIn) { popUpTo(0) }
        }
    }
}
