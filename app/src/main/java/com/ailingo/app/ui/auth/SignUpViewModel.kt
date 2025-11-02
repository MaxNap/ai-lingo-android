package com.ailingo.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailingo.app.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    fun updateEmail(v: String)    { _ui.value = _ui.value.copy(email = v) }
    fun updatePassword(v: String) { _ui.value = _ui.value.copy(password = v) }
    fun updateUsername(v: String) { _ui.value = _ui.value.copy(username = v) }

    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    fun signUp(onSuccess: () -> Unit) = viewModelScope.launch {
        val state = _ui.value
        val email = state.email.trim()
        val password = state.password
        val username = state.username.trim()

        // Client-side validation
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val lengthOK = password.length >= 8
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigitOrSymbol = password.any { it.isDigit() || !it.isLetterOrDigit() }
        val usernameValid = username.isNotEmpty()

        if (!usernameValid) {
            _ui.value = state.copy(error = "Username is required.")
            return@launch
        }
        if (!emailValid) {
            _ui.value = state.copy(error = "Enter a valid email address.")
            return@launch
        }
        if (!(lengthOK && hasUpper && hasLower && hasDigitOrSymbol)) {
            _ui.value = state.copy(
                error = "Password must be 8+ chars and include upper, lower, and a number or symbol."
            )
            return@launch
        }

        _ui.value = state.copy(isLoading = true, error = null)
        try {
            // Repo sets displayName and sends verification email
            repo.signUp(email = email, password = password, username = username)
            onSuccess() // navigate to VerifyEmail screen
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(error = mapAuthError(e))
        } finally {
            _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}
