package com.example.twitterclone.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PostRepository(private val db: FirebaseFirestore) {
    companion object { const val TAG: String = "PostRepository" }

    fun createPost(text: String) {
        val data = hashMapOf(
            "user" to Firebase.auth.currentUser!!.uid,
            "time_posted" to Timestamp.now(),
            "text" to text,
        )
        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Wrote post to collection with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding adding post", e)
            }
    }

    /**
     * Get posts from Firebase backend.
     *
     * @param userUid If specified, will only return posts from user with this UID.
     */
    suspend fun getPosts(userUid: String? = null): List<Post> = coroutineScope {
        var query = db.collection("posts")
            .orderBy("time_posted", Query.Direction.DESCENDING)
        if (userUid != null) {
            query = query.whereEqualTo("user", userUid)
        }

        query.get()
            .await()
            .documents
            .filterNotNull()
            .map { document ->
                async {
                    val userSnapshot = db.collection("users")
                        .document(document["user"] as String)
                        .get()
                        .await()
                    Post.TextPost(
                        user = User(username = userSnapshot["username"] as String),
                        timePosted = document["time_posted"] as Timestamp,
                        text = document["text"] as String
                    )
                }
            }.awaitAll()
    }
}