package com.ailingo.app.ui.auth

fun mapAuthError(e: Exception): String =
    when {
        e.message?.contains("email address is already in use", true) == true -> "Email is already in use."
        e.message?.contains("password is invalid", true) == true -> "Wrong password."
        e.message?.contains("no user record", true) == true -> "User not found."
        e.message?.contains("badly formatted", true) == true -> "Invalid email format."
        e.message?.contains("network error", true) == true -> "Network error. Try again."
        e.message?.contains("WEAK_PASSWORD", true) == true -> "Password is too weak."
        else -> "Authentication failed. ${e.message ?: ""}"
    }
