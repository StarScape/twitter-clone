/**
 * This file defines some common components used across different screens in the App.
 */
package com.example.twitterclone.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twitterclone.data.Post
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatTime(time: Timestamp): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val date = time.toDate()
    return formatter.format(date)
}

@Composable
fun PostContainer(post: Post, content: @Composable () -> Unit) {
    Column(Modifier.padding(8.dp)) {
        Text(text = post.user.username, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        content()
        Spacer(Modifier.height(4.dp))
        Text(formatTime(post.timePosted))
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.dp
        )
    }
}

@Composable
fun TextPost(post: Post.TextPost) {
    PostContainer(post) {
        Text(post.text, modifier = Modifier.padding(start = 2.dp))
    }
}

@Composable
fun ImagePost(post: Post.ImagePost) {
    PostContainer(post) {
        AsyncImage(
            modifier = Modifier.size(size = 240.dp),
            model = post.imageUrl,
            contentDescription = null
        )
        if (post.text != null) {
            Text(post.text, modifier = Modifier.padding(start = 2.dp))
        }
    }
}

/**
 * A Composable for displaying a list of posts.
 */
@Composable
fun Posts(posts: List<Post>, loading: Boolean, onReachedBottomPost: () -> Unit) {
    val listState = rememberLazyListState()
    val reachedBottom: Boolean by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    // Call the onReachedBottomPost callback whenever the user is
    // scrolled all the way to the bottom in order to load more posts.
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onReachedBottomPost()
        }
    }

    LazyColumn(state = listState) {
        items(items = posts, key = { post -> post.uid }) { post ->
            when (post) {
                is Post.TextPost -> TextPost(post)
                is Post.ImagePost -> ImagePost(post)
            }
        }
        if (loading) {
            item {
                Text("Loading...")
//                CircularProgressIndicator(
//                    modifier = Modifier.width(64.dp),
//                )
            }
        }
    }


}
