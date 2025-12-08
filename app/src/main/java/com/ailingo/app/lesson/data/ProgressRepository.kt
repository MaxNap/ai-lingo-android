package com.ailingo.app.lesson.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProgressRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Called when a lesson is completed in the app.
     * This is what your Cloud Function `onProgressWrite` listens to.
     *
     * It writes docs like:
     *   users/{uid}/progress/{courseId}_{lessonId}
     *
     * With fields:
     *   - courseId
     *   - lessonId
     *   - status = "completed"
     *   - completed = true
     *   - xpReward
     */
    suspend fun markLessonCompleted(
        courseId: String,
        lessonId: String,
        xpReward: Int
    ) {
        val uid = auth.currentUser?.uid ?: return

        val docId = "${courseId}_$lessonId"
        val progressRef = db
            .collection("users")
            .document(uid)
            .collection("progress")
            .document(docId)

        progressRef.set(
            mapOf(
                "courseId" to courseId,
                "lessonId" to lessonId,
                "status" to "completed",           // <-- Cloud Function checks this
                "completed" to true,               // <-- and/or this
                "xpReward" to xpReward,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
    }

    /**
     * Stream of lesson completion for a specific course.
     * Emits a map like: { "lesson1" to true, "lesson2" to false, ... }
     */
    fun progressMapForCourse(courseId: String): Flow<Map<String, Boolean>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyMap())
            close()
            return@callbackFlow
        }

        val sub = db.collection("users")
            .document(uid)
            .collection("progress")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                val map = snap.documents.associate { doc ->
                    val lessonId = doc.getString("lessonId") ?: doc.id
                    val completed = doc.getBoolean("completed") == true
                    lessonId to completed
                }

                trySend(map)
            }

        awaitClose {
            sub.remove()
        }
    }
}
