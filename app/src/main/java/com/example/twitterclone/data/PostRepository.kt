package com.example.twitterclone.data

import android.net.Uri
import android.util.Log
import com.example.twitterclone.fileNameForImageUpload
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PostRepository(private val db: FirebaseFirestore) {
    companion object { const val TAG: String = "PostRepository" }

    /**
     * Creates a new text-only post.
     */
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
     * Creates a new image post. The [text] parameter is optional.
     */
    suspend fun createPost(imageFile: Uri, text: String = "") = coroutineScope {
        try {
            val storageRef = Firebase.storage.reference
            val imageRef = storageRef.child("images/${fileNameForImageUpload()}.jpg")
            imageRef.putFile(imageFile).await()

            val downloadUri = imageRef.downloadUrl.await()
            val data = hashMapOf(
                "user" to Firebase.auth.currentUser!!.uid,
                "time_posted" to Timestamp.now(),
                "image_url" to downloadUri.toString(),
                // If there is no text in the post, we
                // still write a blank string to the DB
                "text" to text,
            )
            val documentReference = db.collection("posts")
                .add(data)
                .await()
            Log.i(TAG, "Wrote image post to collection with ID: ${documentReference.id}")
        } catch(e: Exception) {
            Log.e(TAG, "Failed uploading image file with exception: ${e}")
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
                    if (document["image_url"] != null) {
                        Post.ImagePost(
                            user = User(username = userSnapshot["username"] as String),
                            timePosted = document["time_posted"] as Timestamp,
                            imageUrl = document["image_url"] as String,
                            text = document["text"] as String?,
                        )
                    } else {
                        Post.TextPost(
                            user = User(username = userSnapshot["username"] as String),
                            timePosted = document["time_posted"] as Timestamp,
                            text = document["text"] as String
                        )
                    }
                }
            }.awaitAll()
    }
}