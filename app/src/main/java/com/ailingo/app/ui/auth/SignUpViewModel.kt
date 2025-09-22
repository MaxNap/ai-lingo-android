package com.ailingo.app.ui.auth

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

    fun updateEmail(v: String) { _ui.value = _ui.value.copy(email = v) }
    fun updatePassword(v: String) { _ui.value = _ui.value.copy(password = v) }

    fun signUp(onSuccess: () -> Unit) = viewModelScope.launch {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        try {
            repo.signUp(_ui.value.email.trim(), _ui.value.password)
            // Optional: send verification here
            onSuccess()
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(error = mapAuthError(e))
        } finally {
            _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}
