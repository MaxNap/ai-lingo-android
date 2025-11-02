// ui/auth/VerifyEmailViewModel.kt
package com.ailingo.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailingo.app.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class VerifyEmailViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(
        VerifyUiState(email = repo.currentUser?.email.orEmpty())
    )
    val ui = _ui.asStateFlow()

    fun sendVerificationIfNeeded() = viewModelScope.launch {
        // Only send if not verified
        if (repo.isEmailVerified()) return@launch
        safeRun {
            repo.sendEmailVerification()
            startResendCooldown()
        }
    }

    fun resend() = viewModelScope.launch {
        safeRun {
            repo.sendEmailVerification()
            startResendCooldown()
        }
    }

    fun checkVerified(onVerified: () -> Unit) = viewModelScope.launch {
        safeRun(loading = true) {
            repo.reloadUser()
            if (repo.isEmailVerified()) {
                onVerified()
            } else {
                _ui.value = _ui.value.copy(error = "Not verified yet. Please click the link in your email.")
            }
        }
    }

    private fun startResendCooldown(seconds: Int = 60) = viewModelScope.launch {
        for (s in seconds downTo 1) {
            _ui.value = _ui.value.copy(canResend = false, resendSeconds = s)
            delay(1000)
        }
        _ui.value = _ui.value.copy(canResend = true, resendSeconds = 0)
    }

    private suspend fun safeRun(loading: Boolean = false, block: suspend () -> Unit) {
        try {
            if (loading) _ui.value = _ui.value.copy(isLoading = true, error = null)
            block()
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(error = e.message ?: "Something went wrong")
        } finally {
            if (loading) _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}
