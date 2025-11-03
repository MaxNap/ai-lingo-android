package com.ailingo.app.ui.navigation

object Routes {
    // --- Auth flow ---
    const val Splash = "splash"
    const val Welcome = "welcome"
    const val SignIn = "auth/signin"
    const val SignUp = "auth/signup"

    // --- Main ---
    const val Home = "home"

    // --- Learn & Lessons ---
    const val Learn = "learn"                     // Learn list screen
    const val LessonOverview = "lesson_overview"  // Overview before lesson starts
    const val Lesson1 = "lesson/1/1"              // Lesson 1 interactive screen

    // Helper to navigate to a specific overview ID
    fun lessonOverview(id: String) = "$LessonOverview/$id"
}
