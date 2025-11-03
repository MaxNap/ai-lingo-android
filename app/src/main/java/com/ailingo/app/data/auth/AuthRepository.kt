package com.ailingo.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser get() = auth.currentUser
    private val usernameRegex = Regex("^[A-Za-z0-9_]{3,20}$")

    // ✅ --- CHECK USERNAME AVAILABILITY ---
    suspend fun isUsernameAvailable(raw: String): Boolean {
        val uname = raw.trim()
        if (!usernameRegex.matches(uname)) return false
        val key = uname.lowercase()
        val doc = db.collection("usernames").document(key).get().await()
        return !doc.exists()
    }

    // ✅ --- SIGN UP (Atomic with Firestore + Verification) ---
    suspend fun signUp(email: String, password: String, usernameRaw: String) {
        val username = usernameRaw.trim()
        require(usernameRegex.matches(username)) { "Invalid username format" }
        val usernameLower = username.lowercase()

        // Quick check (rules will enforce uniqueness again)
        if (!isUsernameAvailable(username)) error("Username already taken")

        // 1️⃣ Create Firebase Auth user
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("Failed to create account")
        val uid = user.uid

        // 2️⃣ Force a fresh ID token so Firestore sees request.auth (prevents PERMISSION_DENIED)
        user.getIdToken(true).await()

        // 3️⃣ Prepare Firestore batch write
        val batch = db.batch()
        val usernamesRef = db.collection("usernames").document(usernameLower)
        val userRef = db.collection("users").document(uid)

        batch.set(usernamesRef, mapOf(
            "uid" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        ))

        batch.set(userRef, mapOf(
            "username" to username,
            "usernameLower" to usernameLower,
            "email" to email,
            "createdAt" to FieldValue.serverTimestamp(),
            "xp" to 0,
            "streak" to 0,
            "level" to 1,
            "achievements" to emptyList<String>()
        ))

        // 4️⃣ Commit both docs atomically
        batch.commit().await()

        // 5️⃣ Update display name in Auth
        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        ).await()

        // 6️⃣ Send verification email
        user.sendEmailVerification().await()
    }

    // ✅ --- SIGN IN ---
    suspend fun signIn(email: String, password: String) {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        val user = res.user ?: error("User not found")

        if (!user.isEmailVerified) {
            auth.signOut()
            throw IllegalStateException("Please verify your email before logging in.")
        }
    }

    // ✅ --- SIGN OUT ---
    fun signOut() = auth.signOut()

    // ✅ --- RESET PASSWORD ---
    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // ✅ --- EMAIL VERIFICATION HELPERS ---
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
