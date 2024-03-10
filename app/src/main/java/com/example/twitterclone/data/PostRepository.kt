package com.example.twitterclone.data

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

class PostRepository {
    private val db = Firebase.firestore

    suspend fun getPosts(): List<Post> = coroutineScope {
        async {
            db.collection("posts")
                .orderBy("time_posted", Query.Direction.DESCENDING)
                .get()
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
        }.await()
    }
}