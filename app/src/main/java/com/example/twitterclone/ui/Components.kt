/**
 * This file defines some common components used across different screens in the App.
 */
package com.example.twitterclone.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

fun formatTime(time: Timestamp): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val date = time.toDate()
    return formatter.format(date)
}

@Composable
fun TextPost(post: Post.TextPost) {
    Column(Modifier.padding(8.dp)) {
        Text(text = post.user.username, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(post.text, modifier = Modifier.padding(start = 2.dp))
        Spacer(Modifier.height(4.dp))
        Text(formatTime(post.timePosted))
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colors.primaryVariant,
            thickness = 1.dp
        )
    }
}

/**
 * A Composable for displaying a list of posts.
 */
@Composable
fun Posts(posts: List<Post>) {
    LazyColumn {
        items(posts) { post ->
            when (post) {
                is Post.TextPost -> TextPost(post)
                is Post.ImagePost -> TODO()
            }
        }
    }
}

class TimelineViewModel(private val repository: PostRepository) : ViewModel() {
    val posts: MutableState<List<Post>> = mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            posts.value = repository.getPosts()
        }
    }
}