package com.ailingo.app.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val usernameAvailable: Boolean? = null,  // for live check
    val isLoading: Boolean = false,
    val error: String? = null
)
