package com.example.twitterclone.data

import android.net.Uri
import android.util.Log
import com.example.twitterclone.fileNameForImageUpload
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

/**
 * Number of posts on each request.
 */
const val DEFAULT_POSTS_PER_PAGE: Long = 5

class PostRepository(private val db: FirebaseFirestore, private val storage: FirebaseStorage) {
    companion object { const val TAG: String = "PostRepository" }

    /**
     * An type wrapping together the returned list of posts with the [[DocumentSnapshot]]
     * of the last post in the list. This is handy because Firebase requires a snapshot
     * for queries using `.startAfter`, so on subsequent queries to get the next N posts,
     * it saves us from having to query and wait for that doc snapshot again.
     */
    data class PostQueryResult(
        val posts: List<Post>,
        val lastDocumentSnapshot: DocumentSnapshot?
    )

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
            val imageRef = storage.reference.child("images/${fileNameForImageUpload()}.jpg")
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
     * This is a low-level method. For automatically paginating for infinite scroll,
     * see [[getPaginator]].
     *
     * @param userUid If specified, will only return posts from user with this UID.
     * @param afterPostUid If specified, will only return posts after the post with the specified UID.
     * @param limit If specified, will limit query return to N results
     */
    suspend fun getPosts(
        userUid: String? = null,
        limit: Long? = null,
        after: DocumentSnapshot? = null,
    ): PostQueryResult = coroutineScope {
        // delay(2000) // for testing loading indicator
        var query = db.collection("posts")
            .orderBy("time_posted", Query.Direction.DESCENDING)

        if (userUid != null) query = query.whereEqualTo("user", userUid)
        if (after != null) query = query.startAfter(after)
        if (limit != null) query = query.limit(limit)

        val docs = query.get()
            .await()
            .documents
            .filterNotNull()

        val posts = docs.map { document ->
            async {
                // Get the corresponding user record in the user collection for each
                // post, so we can get associated information like the user's name.
                val userSnapshot = db.collection("users")
                    .document(document["user"] as String)
                    .get()
                    .await()
                if (document["image_url"] != null) {
                    // document in DB has image URL, therefore is an image post
                    Post.ImagePost(
                        uid = document.id,
                        user = User(username = userSnapshot["username"] as String),
                        timePosted = document["time_posted"] as Timestamp,
                        imageUrl = document["image_url"] as String,
                        text = document["text"] as String?,
                    )
                } else {
                    // document in DB is a regular ol' text post
                    Post.TextPost(
                        uid = document.id,
                        user = User(username = userSnapshot["username"] as String),
                        timePosted = document["time_posted"] as Timestamp,
                        text = document["text"] as String
                    )
                }
            }
        }.awaitAll()
        val lastDocumentSnapshot = docs.lastOrNull()

        PostQueryResult(posts, lastDocumentSnapshot)
    }

    class PostPaginator(
        private val repository: PostRepository,
        private val N: Long,
        private val userUid: String? = null,
    ) {
        var endReached = false
        private var lastFetched: DocumentSnapshot? = null

        suspend fun getNextN(): List<Post> {
            val result = repository.getPosts(limit = N, after = lastFetched, userUid = userUid)
            lastFetched = result.lastDocumentSnapshot
            if (result.posts.size < N) {
                endReached = true
            }
            return result.posts
        }
    }

    /**
     * Returns a paginator, a stateful object which allows the consuming
     * code to fetch N posts at a time from the backend.
     *
     * @param N - The number of posts to fetch at a time
     * @param userUid - If specified, will only return
     */
    fun getPaginator(N: Long = DEFAULT_POSTS_PER_PAGE, userUid: String? = null): PostPaginator {
        return PostPaginator(this, N, userUid)
    }
}