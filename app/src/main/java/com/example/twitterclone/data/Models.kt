package com.example.twitterclone.data

import android.media.Image
import com.google.firebase.Timestamp
import java.time.Instant

data class User(val username: String)

sealed class Post(val user: User, val timePosted: Timestamp) {
    class TextPost(
        user: User,
        timePosted: Timestamp,
        val text: String
    ) : Post(user, timePosted)
    class ImagePost(
        user: User,
        timePosted: Timestamp,
        val image: Image,
        val text: String?,
    ): Post(user, timePosted)
}
