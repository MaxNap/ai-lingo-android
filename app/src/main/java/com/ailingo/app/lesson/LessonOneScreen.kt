package com.ailingo.app.lesson

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ailingo.app.lesson.ui.LessonScaffold
import com.ailingo.app.lesson.viewmodel.LessonViewModel
import com.ailingo.app.lesson.model.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import com.ailingo.app.lesson.ui.activities.MultipleChoiceCard
import kotlinx.coroutines.delay

@Composable
fun LessonOneScreen(
    onLessonComplete: () -> Unit,
    onBackFromLesson: () -> Unit
) {
    val vm: LessonViewModel = viewModel()
    val act = vm.lesson.activities[vm.index]
    val title = "Lesson 1: ${vm.lesson.title}"

    // Enable "Continue" per-activity using robust signals.
    val isNextEnabled = when (act) {
        is IntroActivity -> true
        is RecapActivity -> true

        is McqActivity -> {
            vm.isCurrentComplete()
        }


        is MatchActivity -> {
            // Allow when all rows are matched (and/or VM already marked done)
            vm.isCurrentComplete() || vm.matchSelections.size == act.rows.size
        }

        is FillBlankActivity -> {
            vm.isCurrentComplete() || (vm.fillChosen == act.correct)
        }

        is FreePromptActivity -> {
            vm.isCurrentComplete() || vm.showMockReply
        }

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
                // Ensure progress is synced before leaving the lesson
                vm.forceSyncCompletion()
                onLessonComplete()
            } else {
                vm.onNext()
            }
        },
        // New (optional) finish button — appears only when whole lesson is complete
        onFinishLesson = {
            vm.forceSyncCompletion()
            onLessonComplete()
        },

        isLessonCompleted = vm.isLessonCompleted,
        syncing = vm.syncing,
        onRetry = { vm.restartLesson() }
    ) {
        when (act) {
            is IntroActivity -> {
                // Mark intro as completed when shown (no interaction needed here)
                LaunchedEffect(vm.index) {
                    vm.markCurrentComplete()
                }

                com.ailingo.app.lesson.ui.activities.IntroCard(
                    heading = act.heading,
                    bullets = act.bullets
                )
            }


            is McqActivity -> {
                MultipleChoiceCard(
                    act = act,
                    onSelect = { opt -> vm.onSelectMcq(opt, act) },
                    feedback = vm.feedback,
                    selectedOptionText = vm.selectedMcqText   // <- NEW
                )

                // Auto-advance 5s after correct so user can read feedback
                LaunchedEffect(vm.index, vm.isCurrentComplete()) {
                    if (vm.isCurrentComplete()) {
                        kotlinx.coroutines.delay(5000)  // 5 seconds
                        if (vm.index < vm.lesson.activities.lastIndex) {
                            vm.onNext()
                        }
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
                // Optional auto-advance after correct fill-in
                LaunchedEffect(vm.index, vm.fillChosen) {
                    if (vm.fillChosen == act.correct) {
                        delay(5000)
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
                // When user reaches Recap, mark the final step complete
                LaunchedEffect(vm.index) {
                    vm.markCurrentComplete()
                }

                com.ailingo.app.lesson.ui.activities.RecapCard(act)
            }

        }
    }
}
