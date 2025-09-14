package com.ailingo.app.lesson.data

import com.ailingo.app.lesson.model.*

object HardcodedLessons {
    val lesson1 = LessonDefinition(
        id = "unit1_lesson1",
        unitId = "unit1",
        title = "What is a Prompt?",
        activities = listOf(
            IntroActivity(
                heading = "What is a Prompt?",
                bullets = listOf(
                    "A prompt is the message you give to an AI to tell it what you want.",
                    "It can be a question or an instruction.",
                    "Example:\n❌ “Weather?” → too short\n✅ “What’s the weather in Calgary today?” → clear prompt"
                )
            ),
            McqActivity(
                question = "A prompt is…",
                options = listOf(
                    McqOption("Your message to the AI", true),
                    McqOption("The answer from the AI", false),
                    McqOption("The name of the app", false)
                ),
                feedbackCorrect = "🎉 Yes! A prompt is what you type to the AI.",
                feedbackIncorrect = "Not quite. A prompt is what you send, not what AI gives back."
            ),
            MatchActivity(
                instruction = "Match the examples with the right side.",
                rightChoices = listOf("Prompt", "Not Prompt"),
                rows = listOf(
                    MatchRow("Write me a 3-sentence story about a dog.", "Prompt"),
                    MatchRow("2 + 2 = 4", "Not Prompt"),
                    MatchRow("Plan a workout for chest and arms.", "Prompt")
                ),
                wrongHint = "A prompt is something you ask the AI to do."
            ),
            FillBlankActivity(
                sentenceWithBlank = "A _____ is the instruction or question you give to the AI.",
                options = listOf("prompt", "answer", "result"),
                correct = "prompt"
            ),
            FreePromptActivity(
                instruction = "Write your own prompt asking AI about food.",
                minChars = 10,
                hint = "Try a clearer, more specific prompt.",
                mockReply = """
                    🍝 Here are 3 quick pasta recipes:
                    1) Garlic butter spaghetti
                    2) Tomato-basil penne
                    3) Creamy mushroom fusilli
                """.trimIndent()
            ),
            RecapActivity(
                bullets = listOf(
                    "A prompt is your message to the AI.",
                    "Good prompts are clear and specific.",
                    "You can ask questions or give instructions."
                ),
                rewardXp = 10,
                rewardStars = 1,
                nextLessonHint = "Unlocks: Lesson 2 — Saying Hello to AI."
            )
        )
    )
}
