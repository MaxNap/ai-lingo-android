package com.ailingo.app.lesson.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailingo.app.lesson.data.HardcodedLessons
import com.ailingo.app.lesson.data.ProgressRepository
import com.ailingo.app.lesson.model.FillBlankActivity
import com.ailingo.app.lesson.model.FreePromptActivity
import com.ailingo.app.lesson.model.LessonDefinition
import com.ailingo.app.lesson.model.MatchActivity
import com.ailingo.app.lesson.model.McqActivity
import com.ailingo.app.lesson.model.McqOption
import kotlinx.coroutines.launch

class LessonViewModel(
    private val courseId: String = "courseA",                 // adjust if you have courses
    private val progressRepo: ProgressRepository = ProgressRepository(),
) : ViewModel() {

    // ----- Lesson & progress state -----
    var wrongMatchRowIndex by mutableStateOf<Int?>(null)
        private set

    var wrongMatchChoice by mutableStateOf<String?>(null)
        private set

    var lesson: LessonDefinition by mutableStateOf(HardcodedLessons.lesson1)
        private set

    /** Stable id for Firestore keying; update when changing lessons */
    private var lessonId: String by mutableStateOf("lesson1")

    var index by mutableStateOf(0)
        private set

    var hearts by mutableStateOf(3)
        private set

    /** Per-activity completion flags */
    private var completed = BooleanArray(lesson.activities.size) { false }

    /** Completion state for the *current* activity (used for Continue button) */
    private var currentCompleted by mutableStateOf(false)

    /** Prevent double write; pairs with `syncing` UI flag */
    private var completionSynced = false
    var syncing by mutableStateOf(false)
        private set

    // ----- Transient UI -----
    var selectedMcqText by mutableStateOf<String?>(null)
        private set

    var feedback by mutableStateOf<String?>(null)
        private set

    val matchSelections = mutableStateMapOf<Int, String>()
    var fillChosen: String? by mutableStateOf(null)
        private set

    var userFreePrompt by mutableStateOf("")
        private set
    var showMockReply by mutableStateOf(false)
        private set

    val progress: Float
        get() = completed.count { it }.toFloat() / lesson.activities.size

    /** Used by the UI to decide if the Continue button is enabled */
    fun isCurrentComplete(): Boolean = currentCompleted

    val isLessonCompleted: Boolean
        get() = completed.all { it }

    // ----- Lesson switching -----
    fun loadLessonTwo() {
        lesson = HardcodedLessons.lesson2
        lessonId = "lesson2"
        index = 0
        hearts = 3
        completed = BooleanArray(lesson.activities.size) { false }
        completionSynced = false
        syncing = false
        resetTransientForIndex(0)
    }
    fun restartLesson() {
        index = 0
        hearts = 3
        completed = BooleanArray(lesson.activities.size) { false }
        completionSynced = false
        syncing = false
        resetTransientForIndex(0)
    }


    // Generic setter if you add more lessons
    fun loadLesson(id: String, def: LessonDefinition) {
        lesson = def
        lessonId = id
        index = 0
        hearts = 3
        completed = BooleanArray(lesson.activities.size) { false }
        completionSynced = false
        syncing = false
        resetTransientForIndex(0)
    }

    // ----------------- Helpers -----------------
    private fun resetTransientForIndex(newIndex: Int) {
        feedback = null
        matchSelections.clear()
        fillChosen = null
        userFreePrompt = ""
        showMockReply = false
        selectedMcqText = null
        wrongMatchRowIndex = null
        wrongMatchChoice = null

        // Restore completion state for this activity
        currentCompleted = completed.getOrNull(newIndex) == true
    }

    private fun markDone() {
        completed[index] = true
        currentCompleted = true
        maybeSyncCompletion()
    }

    /** Allow UI to mark the current activity as completed (Intro/Recap, etc.). */
    fun markCurrentComplete() {
        if (!currentCompleted) {
            markDone()
        }
    }

    /** If the lesson is fully completed, write progress to Firestore (idempotent). */
    private fun maybeSyncCompletion() {
        if (!isLessonCompleted || completionSynced || syncing) return
        syncing = true
        completionSynced = true

        viewModelScope.launch {
            try {
                // default XP = 10 (tweak per-lesson if needed)
                progressRepo.markLessonCompleted(
                    courseId = courseId,
                    lessonId = lessonId,
                    xpReward = 10
                )
            } catch (t: Throwable) {
                // allow retry if write failed
                completionSynced = false
                feedback = t.message ?: "Failed to save progress"
            } finally {
                syncing = false
            }
        }
    }

    /** Called from the Recap screen / Finish button.
     *  Marks all activities as done and syncs progress once.
     */
    fun forceSyncCompletion() {
        // Treat reaching the recap as finishing the lesson
        completed.fill(true)
        currentCompleted = true
        maybeSyncCompletion()
    }

    // ----------------- Navigation -----------------
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
        if (currentCompleted) return

        // remember which option user tapped (for red highlight)
        selectedMcqText = option.text

        if (option.correct) {
            // ✅ This is what unlocks the Continue button
            feedback = act.feedbackCorrect
            markDone()
        } else {
            hearts = (hearts - 1).coerceAtLeast(0)
            feedback = act.feedbackIncorrect
        }
    }

    /* MATCH */
    fun onMatchPick(rowIndex: Int, rightChoice: String, act: MatchActivity) {
        if (currentCompleted) return

        val expected = act.rows[rowIndex].rightCorrect
        if (rightChoice == expected) {
            matchSelections[rowIndex] = rightChoice

            val allCorrect = act.rows.indices.all { i ->
                matchSelections[i] == act.rows[i].rightCorrect
            }

            if (allCorrect) {
                feedback = "Great matching!"
                markDone()
            } else {
                feedback = "Correct! Keep going."
            }
        } else {
            hearts = (hearts - 1).coerceAtLeast(0)

            // Remember WRONG choice for UI coloring
            wrongMatchRowIndex = rowIndex
            wrongMatchChoice = rightChoice
        }
    }

    /* FILL BLANK */
    fun onFillSelect(choice: String, act: FillBlankActivity) {
        if (currentCompleted) return
        fillChosen = choice
        if (choice == act.correct) {
            feedback = "Nice! \"$choice\" is correct."
            markDone()
        } else {
            hearts = (hearts - 1).coerceAtLeast(0)
            feedback = "Try again."
        }
    }

    /* FREE PROMPT */
    fun onFreePromptChange(text: String) {
        userFreePrompt = text
    }

    fun onFreePromptSubmit(act: FreePromptActivity) {
        if (currentCompleted) return
        if (userFreePrompt.trim().length < act.minChars) {
            feedback = act.hint
            return
        }
        showMockReply = true
        feedback = "Nice! That's a clear prompt."
        markDone()
    }
}
