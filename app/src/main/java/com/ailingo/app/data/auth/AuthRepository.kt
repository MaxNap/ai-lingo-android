package com.ailingo.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser get() = auth.currentUser

    // --- SIGN UP ---
    suspend fun signUp(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).await()

        // ✅ Set display name (username)
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        )?.await()

        // ✅ Send email verification right after sign up
        auth.currentUser?.sendEmailVerification()?.await()
    }

    // --- SIGN IN ---
    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    // --- SIGN OUT ---
    fun signOut() = auth.signOut()

    // --- RESET PASSWORD ---
    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // --- EMAIL VERIFICATION HELPERS ---
    suspend fun sendEmailVerification() {
        currentUser?.sendEmailVerification()?.await()
    }

    suspend fun reloadUser() {
        currentUser?.reload()?.await()
    }

    fun isEmailVerified(): Boolean {
        return currentUser?.isEmailVerified == true
    }
}
