package com.example.twitterclone.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.twitterclone.data.Post
import com.example.twitterclone.ui.TimelineViewModel
import com.google.firebase.Timestamp
import org.koin.compose.koinInject
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

@Composable
fun TimelineScreen(timelineViewModel: TimelineViewModel = koinInject()) {
    val posts by timelineViewModel.posts.collectAsState()
    LazyColumn {
        items(posts) { post ->
            when (post) {
                is Post.TextPost -> TextPost(post)
                is Post.ImagePost -> TODO()
            }
        }
    }
}