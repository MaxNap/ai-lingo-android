package com.ailingo.app.lesson

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ailingo.app.lesson.model.*
import com.ailingo.app.lesson.ui.LessonScaffold
import com.ailingo.app.lesson.viewmodel.LessonViewModel
import kotlinx.coroutines.delay

@Composable
fun LessonTwoScreen(
    onLessonComplete: () -> Unit,
    onBackFromLesson: () -> Unit
) {
    val vm: LessonViewModel = viewModel()

    // Load Lesson 2 data
    LaunchedEffect(Unit) {
        vm.loadLessonTwo()
    }

    val act = vm.lesson.activities[vm.index]
    val title = "Lesson 2: ${vm.lesson.title}"

    // Enable "Continue" per-activity
    val isNextEnabled = when (act) {
        is IntroActivity -> true
        is RecapActivity -> true

        is McqActivity -> vm.isCurrentComplete()

        is MatchActivity ->
            vm.isCurrentComplete() || vm.matchSelections.size == act.rows.size

        is FillBlankActivity ->
            vm.isCurrentComplete() || (vm.fillChosen == act.correct)

        is FreePromptActivity ->
            vm.isCurrentComplete() || vm.showMockReply

        else -> vm.isCurrentComplete()
    }

    LessonScaffold(
        title = title,
        hearts = vm.hearts,
        progress = vm.progress,
        isNextEnabled = isNextEnabled,
        onBack = {
            if (vm.index == 0) onBackFromLesson() else vm.onBack()
        },
        onNext = {
            if (vm.index == vm.lesson.activities.lastIndex) {
                // ✅ Save progress before leaving
                vm.forceSyncCompletion()
                onLessonComplete()
            } else {
                vm.onNext()
            }
        },

        // ✅ Finish button (only shows when whole lesson complete)
        onFinishLesson = {
            vm.forceSyncCompletion()
            onLessonComplete()
        },
        isLessonCompleted = vm.isLessonCompleted,
        syncing = vm.syncing,

        // ✅ Try again when hearts == 0 (LessonScaffold shows it)
        onRetry = { vm.restartLesson() }
    ) {
        when (act) {
            is IntroActivity -> {
                // ✅ Mark intro as completed when shown (no interaction needed)
                LaunchedEffect(vm.index) { vm.markCurrentComplete() }

                com.ailingo.app.lesson.ui.activities.IntroCard(
                    heading = act.heading,
                    bullets = act.bullets
                )
            }

            is McqActivity -> {
                com.ailingo.app.lesson.ui.activities.MultipleChoiceCard(
                    act = act,
                    onSelect = { opt -> vm.onSelectMcq(opt, act) },
                    feedback = vm.feedback,
                    selectedOptionText = vm.selectedMcqText
                )

                // Optional auto-advance after correct
                LaunchedEffect(vm.index, vm.isCurrentComplete()) {
                    if (vm.isCurrentComplete()) {
                        delay(1500)
                        if (vm.index < vm.lesson.activities.lastIndex) vm.onNext()
                    }
                }
            }

            is MatchActivity -> {
                com.ailingo.app.lesson.ui.activities.MatchPairsCard(
                    act = act,
                    currentMatches = vm.matchSelections,
                    onPick = { i, choice -> vm.onMatchPick(i, choice, act) },
                    feedback = vm.feedback,
                    wrongMatchRowIndex = vm.wrongMatchRowIndex,
                    wrongMatchChoice = vm.wrongMatchChoice
                )
            }

            is FillBlankActivity -> {
                com.ailingo.app.lesson.ui.activities.FillBlankCard(
                    act = act,
                    chosen = vm.fillChosen,
                    onChoose = { s -> vm.onFillSelect(s, act) },
                    feedback = vm.feedback
                )

                // Optional auto-advance after correct fill
                LaunchedEffect(vm.index, vm.fillChosen) {
                    if (vm.fillChosen == act.correct) {
                        delay(1500)
                        if (vm.index < vm.lesson.activities.lastIndex) vm.onNext()
                    }
                }
            }

            is FreePromptActivity -> {
                com.ailingo.app.lesson.ui.activities.FreePromptPracticeCard(
                    act = act,
                    userText = vm.userFreePrompt,
                    onChange = vm::onFreePromptChange,
                    onSubmit = { vm.onFreePromptSubmit(act) },
                    showMockReply = vm.showMockReply,
                    feedback = vm.feedback
                )
            }

            is RecapActivity -> {
                // ✅ Mark recap as completed when shown (so lesson completes)
                LaunchedEffect(vm.index) { vm.markCurrentComplete() }

                com.ailingo.app.lesson.ui.activities.RecapCard(act)
            }
        }
    }
}
