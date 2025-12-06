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
    const val Learn = "learn"                         // Learn tab screen

    // --- Lesson Overview (unit overview before lessons) ---
    const val LessonOverviewBase = "lesson_overview"  // Base route for a unit overview

    // Navigate to lesson overview for a specific unit
    fun lessonOverview(unitId: String): String =
        "$LessonOverviewBase/$unitId"


    // --- Individual Lessons ---
    // We avoid hardcoding "lesson/1/1" – use a template instead.
    private const val LessonBase = "lesson"

    /**
     * Build a lesson route:
     *  unitId = "1"
     *  lessonId = "1"
     *
     * Produces: "lesson/1/1"
     */
    fun lesson(unitId: String, lessonId: String): String =
        "$LessonBase/$unitId/$lessonId"


    // Example convenience constants (optional)
    // You can keep these if Lesson 1 is common:
    val Lesson1 = lesson("1", "1") // -> "lesson/1/1"
}
