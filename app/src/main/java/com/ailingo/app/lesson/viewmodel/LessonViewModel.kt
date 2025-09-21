package com.ailingo.app.lesson.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ailingo.app.lesson.data.HardcodedLessons
import com.ailingo.app.lesson.model.FillBlankActivity
import com.ailingo.app.lesson.model.FreePromptActivity
import com.ailingo.app.lesson.model.LessonDefinition
import com.ailingo.app.lesson.model.MatchActivity
import com.ailingo.app.lesson.model.McqActivity
import com.ailingo.app.lesson.model.McqOption

class LessonViewModel : ViewModel() {

    // Current lesson definition (changed from val to var to support multiple lessons)
    var lesson: LessonDefinition by mutableStateOf(HardcodedLessons.lesson1)
        private set

    // Index of the current activity
    var index by mutableStateOf(0)
        private set

    // Lives / hearts
    var hearts by mutableStateOf(3)
        private set

    // Per-activity completion flags (changed from val to var to support lesson switching)
    private var completed = BooleanArray(lesson.activities.size) { false }

    // Transient UI feedback string (success/error/hints)
    var feedback by mutableStateOf<String?>(null)
        private set

    // --- Activity-specific transient state ---

    // MATCH: reactive map (rowIndex -> "Prompt"/"Not Prompt")
    // Using mutableStateMapOf so Compose will recompose when entries change.
    val matchSelections = mutableStateMapOf<Int, String>()

    // FILL BLANK: current chosen option
    var fillChosen: String? by mutableStateOf(null)
        private set

    // FREE PROMPT: user input + reply visibility
    var userFreePrompt by mutableStateOf("")
        private set
    var showMockReply by mutableStateOf(false)
        private set

    // Overall progress (0..1)
    val progress: Float
        get() = completed.count { it }.toFloat() / lesson.activities.size

    fun isCurrentComplete(): Boolean = completed[index]

    // NEW: Load Lesson 2 function
    fun loadLessonTwo() {
        lesson = HardcodedLessons.lesson2
        index = 0
        hearts = 3
        completed = BooleanArray(lesson.activities.size) { false }
        resetTransientForIndex(0)
    }

    // ----------------- Navigation helpers -----------------

    private fun resetTransientForIndex(@Suppress("UNUSED_PARAMETER") newIndex: Int) {
        // Clear per-step UI state so each card starts fresh
        feedback = null
        matchSelections.clear()
        fillChosen = null
        userFreePrompt = ""
        showMockReply = false
    }

    private fun markDone() {
        completed[index] = true
    }

    fun onNext() {
        if (index < lesson.activities.size - 1) {
            index++
            resetTransientForIndex(index)
        }
    }

    fun onBack() {
        if (index > 0) {
            index--
            resetTransientForIndex(index)
        }
    }

    // ----------------- Activity handlers -----------------

    /* MCQ */
    fun onSelectMcq(option: McqOption, act: McqActivity) {
        if (completed[index]) return
        if (option.correct) {
            feedback = act.feedbackCorrect
            markDone()
        } else {
            if (hearts > 0) hearts--
            feedback = act.feedbackIncorrect
        }
    }

    /* MATCH */
    fun onMatchPick(rowIndex: Int, rightChoice: String, act: MatchActivity) {
        if (completed[index]) return

        val expected = act.rows[rowIndex].rightCorrect
        if (rightChoice == expected) {
            // Update reactive selection state
            matchSelections[rowIndex] = rightChoice

            // Check if all pairs are correctly matched
            val allCorrect = act.rows.indices.all { i ->
                matchSelections[i] == act.rows[i].rightCorrect
            }

            if (allCorrect) {
                markDone()
                feedback = "Great matching! ✅"
            } else {
                feedback = "Correct! Keep going."
            }
        } else {
            if (hearts > 0) hearts--
            feedback = "❌ ${act.wrongHint}"
        }
    }

    /* FILL BLANK */
    fun onFillSelect(choice: String, act: FillBlankActivity) {
        if (completed[index]) return
        fillChosen = choice
        if (choice == act.correct) {
            markDone()
            feedback = "✅ Nice! \"$choice\" is correct."
        } else {
            if (hearts > 0) hearts--
            feedback = "❌ Try again."
        }
    }

    /* FREE PROMPT */
    fun onFreePromptChange(text: String) {
        userFreePrompt = text
    }

    fun onFreePromptSubmit(act: FreePromptActivity) {
        if (completed[index]) return
        if (userFreePrompt.trim().length < act.minChars) {
            feedback = act.hint
            return
        }
        showMockReply = true
        markDone()
        feedback = "Nice! That's a clear prompt. ✅"
    }
}