package com.ailingo.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailingo.app.data.auth.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.ailingo.app.ui.auth.AuthUiState



class SignUpViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    private val usernameRegex = Regex("^[A-Za-z0-9_]{3,20}$")
    private var usernameCheckJob: Job? = null

    fun updateEmail(v: String)    { _ui.value = _ui.value.copy(email = v) }
    fun updatePassword(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun updateUsername(v: String) {
        _ui.value = _ui.value.copy(username = v, usernameAvailable = null, error = null)

        // debounce availability check
        usernameCheckJob?.cancel()
        usernameCheckJob = viewModelScope.launch {
            val candidate = v.trim()

            // basic format gate
            if (!usernameRegex.matches(candidate)) {
                if (candidate.isEmpty()) {
                    // empty: UI can hide helper
                    _ui.value = _ui.value.copy(usernameAvailable = null)
                } else {
                    // invalid format → mark as unavailable so button can disable
                    _ui.value = _ui.value.copy(usernameAvailable = false)
                }
                return@launch
            }

            delay(400) // debounce

            runCatching { repo.isUsernameAvailable(candidate) }
                .onSuccess { ok ->
                    _ui.value = _ui.value.copy(usernameAvailable = ok)
                }
                .onFailure { e ->
                    // network or rules error — keep null to avoid false negatives
                    _ui.value = _ui.value.copy(usernameAvailable = null, error = e.message)
                }
        }
    }

    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    fun signUp(onSuccess: () -> Unit) = viewModelScope.launch {
        val state = _ui.value
        val email = state.email.trim()
        val password = state.password
        val username = state.username.trim()

        // --- Client-side validation ---
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val lengthOK = password.length >= 8
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigitOrSymbol = password.any { it.isDigit() || !it.isLetterOrDigit() }
        val passValid = lengthOK && hasUpper && hasLower && hasDigitOrSymbol

        if (!usernameRegex.matches(username)) {
            _ui.value = state.copy(error = "Username must be 3–20 chars (letters, numbers, or _).")
            return@launch
        }
        if (!emailValid) {
            _ui.value = state.copy(error = "Enter a valid email address.")
            return@launch
        }
        if (!passValid) {
            _ui.value = state.copy(
                error = "Password must be 8+ chars and include upper, lower, and a number or symbol."
            )
            return@launch
        }

        _ui.value = state.copy(isLoading = true, error = null)

        try {
            // Final availability check to avoid race conditions (in case user typed just before tap)
            val available = runCatching { repo.isUsernameAvailable(username) }.getOrElse { false }
            if (!available) {
                _ui.value = _ui.value.copy(isLoading = false, usernameAvailable = false, error = "Username already taken.")
                return@launch
            }

            // Repo performs atomic write (Auth + Firestore + verification email)
            repo.signUp(email = email, password = password, usernameRaw = username)


            onSuccess() // navigate to "Verify Email" screen
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(error = mapAuthError(e))
        } finally {
            _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}

// If you have a central mapper already, keep using it.
private fun mapAuthError(e: Throwable): String {
    val msg = e.message ?: return "Sign up failed."
    return when {
        msg.contains("email address is already in use", ignoreCase = true) -> "This email is already registered."
        msg.contains("WEAK_PASSWORD", ignoreCase = true) -> "Your password is too weak."
        msg.contains("INVALID_EMAIL", ignoreCase = true) -> "Invalid email address."
        else -> msg
    }
}
