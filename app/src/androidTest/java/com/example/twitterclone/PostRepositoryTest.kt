package com.example.twitterclone

import android.net.Uri
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import com.example.twitterclone.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.awaits
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Test

class PostRepositoryTest {

    private val db: FirebaseFirestore = mockk()
    private val auth: FirebaseAuth = mockk()
    private val storage: FirebaseStorage = mockk()
    private val repository = PostRepository(db, storage, auth)

    @Test
    fun create_text_post() = runBlocking {
        mockkStatic(Timestamp::class)
        val mockTimestamp = Timestamp.now()
        every { Timestamp.now() } returns mockTimestamp

        every { auth.currentUser } returns mockk {
            every { uid } returns "user123"
        }
        every { db.collection("posts").add(any()) } returns mockk()

        val sampleText = "Sample text"
        repository.createPost(sampleText)

        verify(exactly = 1) {
            db.collection("posts").add(
                hashMapOf(
                    "text" to sampleText,
                    "time_posted" to mockTimestamp,
                    "user" to "user123"
                )
            )
        }
    }

    @Test
    fun test_createPost_with_image(): Unit = runBlocking {
        mockkStatic(Timestamp::class)
        val mockTimestamp = Timestamp.now()
        every { Timestamp.now() } returns mockTimestamp

        every { auth.currentUser } returns mockk {
            every { uid } returns "user123"
        }
        val imageUri = mockk<Uri>()
        val imageRef = mockk<StorageReference>()
        every { storage.reference.child(any()) } returns imageRef
        every { imageRef.putFile(any()) } returns mockUploadTask()
        every { imageRef.downloadUrl } returns mockTask(imageUri)

        val documentRef: DocumentReference = mockk {
            every { id } returns "example_id"
        }
        every { db.collection("posts").add(any()) } returns mockTask(documentRef)

        repository.createPost(imageUri, "Sample text")

        // Verify DB is called with expected arguments
        verify(exactly = 1) {
            db.collection("posts").add(
                hashMapOf(
                    "text" to "Sample text",
                    "time_posted" to mockTimestamp,
                    "user" to "user123",
                    "image_url" to imageUri.toString(),
                )
            )
        }
    }

    @Test
    fun paginator_test(): Unit = runBlocking {
        val user = User(username = "kevin")

        val page1 = listOf(
            Post.TextPost(user, Timestamp.now(), "1", "post1"),
            Post.TextPost(user, Timestamp.now(), "2", "post2"),
            Post.TextPost(user, Timestamp.now(), "3", "post3"),
        )
        val page2 = listOf(
            Post.TextPost(user, Timestamp.now(), "4", "post4"),
            Post.TextPost(user, Timestamp.now(), "5", "post5"),
            Post.TextPost(user, Timestamp.now(), "6", "post6"),
        )
        val page3 = listOf(
            Post.TextPost(user, Timestamp.now(), "7", "post7"),
            Post.TextPost(user, Timestamp.now(), "8", "post8"),
            Post.TextPost(user, Timestamp.now(), "9", "post9"),
        )

        // Mock of getPosts method that returns the three pages of posts above, in order,
        // so that we can test the logic in PagePaginator independent of the networking calls.
        var call = 1
        val mockedRepository: PostRepository = mockk() {
            coEvery { getPosts(any(), any(), any()) } answers {
                val snapshot: DocumentSnapshot = mockk<DocumentSnapshot>()
                when (call++) {
                    1 -> PostRepository.PostQueryResult(page1, snapshot)
                    2 -> PostRepository.PostQueryResult(page2, snapshot)
                    3 -> PostRepository.PostQueryResult(page3, snapshot)
                    else -> PostRepository.PostQueryResult(listOf(), null)
                }
            }
        }

        val paginator = PostRepository.PostPaginator(mockedRepository, 3)
        assertEquals(paginator.getNextN(), page1)
        assertEquals(paginator.getNextN(), page2)
        assertEquals(paginator.getNextN(), page3)
        assertEquals(paginator.getNextN(), listOf<Post>())
        assertEquals(paginator.endReached, true)
    }
}
