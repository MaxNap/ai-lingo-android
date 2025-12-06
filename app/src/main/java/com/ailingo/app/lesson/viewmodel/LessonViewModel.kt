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
    var lesson: LessonDefinition by mutableStateOf(HardcodedLessons.lesson1)
        private set

    /** Stable id for Firestore keying; update when changing lessons */
    private var lessonId: String by mutableStateOf("lesson1")

    var index by mutableStateOf(0)
        private set

    var hearts by mutableStateOf(3)
        private set

    private var completed = BooleanArray(lesson.activities.size) { false }

    /** Prevent double write; pairs with `syncing` UI flag */
    private var completionSynced = false
    var syncing by mutableStateOf(false)
        private set

    // ----- Transient UI -----
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

    fun isCurrentComplete(): Boolean = completed[index]
    val isLessonCompleted: Boolean
        get() = completed.all { it }

    // ----- Lesson switching (kept, but now sets lessonId explicitly) -----
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

    // If you later add more lessons, expose a generic setter:
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
    private fun resetTransientForIndex(@Suppress("UNUSED_PARAMETER") newIndex: Int) {
        feedback = null
        matchSelections.clear()
        fillChosen = null
        userFreePrompt = ""
        showMockReply = false
    }

    private fun markDone() {
        completed[index] = true
        maybeSyncCompletion()
    }

    /** If the lesson is fully completed, write progress to Firestore (idempotent). */
    private fun maybeSyncCompletion() {
        if (!isLessonCompleted || completionSynced || syncing) return
        syncing = true
        completionSynced = true

        viewModelScope.launch {
            try {
                // default XP = 10 (tweak per-lesson if needed)
                progressRepo.markLessonCompleted(courseId = courseId, lessonId = lessonId, xpReward = 10)
                // success: nothing else to do; Learn screen should react via its listener
            } catch (t: Throwable) {
                // allow retry if write failed (e.g., emulator not running yet)
                completionSynced = false
                feedback = t.message ?: "Failed to save progress"
            } finally {
                syncing = false
            }
        }
    }

    /** Optional CTA hook for a "Finish lesson" button to force the write now. */
    fun forceSyncCompletion() {
        if (isLessonCompleted) {
            maybeSyncCompletion()
        } else {
            // If you want the button to auto-complete remaining steps, uncomment:
            // completed.fill(true)
            // maybeSyncCompletion()
        }
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

    // ----------------- Activity handlers (unchanged in behavior) -----------------
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
            matchSelections[rowIndex] = rightChoice

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
