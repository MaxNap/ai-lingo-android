package com.ailingo.app.lesson.model

data class LessonUi(
    val courseId: String,
    val lessonId: String,
    val title: String,
    val xpReward: Int,
    val completed: Boolean
)
