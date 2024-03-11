package com.example.twitterclone.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val db: FirebaseFirestore) {
    companion object { const val TAG: String = "PostRepository" }

    /**
     * Get user information from Firebase backend.
     */
    suspend fun getUser(userUid: String): User {
        val snapshot = db.collection("users")
            .document(userUid)
            .get()
            .await()
        return User(
            username = snapshot["username"] as String,
        )
    }
}