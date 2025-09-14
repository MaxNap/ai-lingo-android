package com.ailingo.app.lesson.model

// Activity types we support in this lesson.
enum class LessonActivityType { INTRO, MCQ, MATCH, FILL_BLANK, FREE_PROMPT, RECAP }

data class LessonDefinition(
    val id: String,            // e.g., "unit1_lesson1"
    val unitId: String,        // e.g., "unit1"
    val title: String,         // "What is a Prompt?"
    val activities: List<LessonActivity>
)

sealed interface LessonActivity {
    val type: LessonActivityType
}

/* INTRO */
data class IntroActivity(
    val heading: String,
    val bullets: List<String>
): LessonActivity { override val type = LessonActivityType.INTRO }

/* MCQ */
data class McqOption(val text: String, val correct: Boolean)
data class McqActivity(
    val question: String,
    val options: List<McqOption>,
    val feedbackCorrect: String,
    val feedbackIncorrect: String
): LessonActivity { override val type = LessonActivityType.MCQ }

/* MATCH */
data class MatchRow(
    val leftText: String,
    val rightCorrect: String // "Prompt" or "Not Prompt"
)
data class MatchActivity(
    val instruction: String,
    val rightChoices: List<String>, // ["Prompt","Not Prompt"]
    val rows: List<MatchRow>,
    val wrongHint: String
): LessonActivity { override val type = LessonActivityType.MATCH }

/* FILL BLANK */
data class FillBlankActivity(
    val sentenceWithBlank: String, // "A _____ is the instruction ..."
    val options: List<String>,     // ["prompt","answer","result"]
    val correct: String
): LessonActivity { override val type = LessonActivityType.FILL_BLANK }

/* FREE PROMPT */
data class FreePromptActivity(
    val instruction: String,
    val minChars: Int,
    val hint: String,
    val mockReply: String
): LessonActivity { override val type = LessonActivityType.FREE_PROMPT }

/* RECAP */
data class RecapActivity(
    val bullets: List<String>,
    val rewardXp: Int,
    val rewardStars: Int,
    val nextLessonHint: String
): LessonActivity { override val type = LessonActivityType.RECAP }
