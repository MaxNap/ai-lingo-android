package com.ailingo.app.lesson.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProgressRepository {

    // Make these clear class properties so there's no confusion
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /** Emits lessonId -> completed for the given courseId. */
    fun progressMapForCourse(courseId: String): Flow<Map<String, Boolean>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyMap())
            close()
            return@callbackFlow
        }

        val col = db.collection("users")
            .document(uid)
            .collection("progress")

        val reg: ListenerRegistration = col.addSnapshotListener { qs, err ->
            if (err != null) {
                // On any error (rules, offline, emulator not running), emit empty to keep UI stable
                trySend(emptyMap())
                return@addSnapshotListener
            }

            val map = mutableMapOf<String, Boolean>()
            qs?.documents?.forEach { d ->
                if (d.id.startsWith("${courseId}_")) {
                    val id = d.id.removePrefix("${courseId}_")
                    val done =
                        (d.getString("status") == "completed") ||
                                (d.getBoolean("finalized") == true)
                    map[id] = done
                }
            }
            trySend(map)
        }

        awaitClose { reg.remove() }
    }

    /**
     * Mark as completed (idempotent).
     * Uses await() so callers can safely show/hide loading states.
     */
    suspend fun markLessonCompleted(
        courseId: String,
        lessonId: String,
        xpReward: Int = 10
    ) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.collection("users")
            .document(uid)
            .collection("progress")
            .document("${courseId}_$lessonId")

        val data = mapOf(
            "status" to "completed",
            "xpReward" to xpReward,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        // Merge ensures we don't clobber fields your Cloud Function may add later
        ref.set(data, SetOptions.merge()).await()
    }
}
