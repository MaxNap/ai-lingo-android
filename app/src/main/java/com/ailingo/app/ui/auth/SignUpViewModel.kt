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
    fun updateUsername(v: String) { _ui.value = _ui.value.copy(username = v) }

    fun signUp(onSuccess: () -> Unit) = viewModelScope.launch {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        try {
            repo.signUp(
                email = _ui.value.email.trim(),
                password = _ui.value.password,
                username = _ui.value.username.trim()
            )
            onSuccess()
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(error = mapAuthError(e))
        } finally {
            _ui.value = _ui.value.copy(isLoading = false)
        }
    }
}
