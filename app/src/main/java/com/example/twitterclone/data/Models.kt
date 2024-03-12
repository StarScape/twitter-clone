package com.example.twitterclone.data

import android.media.Image
import com.google.firebase.Timestamp
import java.time.Instant

data class User(val username: String)

sealed class Post(val user: User, val timePosted: Timestamp, val uid: String) {
    class TextPost(
        user: User,
        timePosted: Timestamp,
        uid: String,
        val text: String,
    ) : Post(user, timePosted, uid)

    class ImagePost(
        user: User,
        timePosted: Timestamp,
        uid: String,
        val imageUrl: String,
        val text: String?,
    ): Post(user, timePosted, uid)
}
