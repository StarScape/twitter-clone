package com.example.twitterclone.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.twitterclone.ui.Posts
import com.example.twitterclone.ui.viewmodels.TimelineViewModel
import org.koin.compose.koinInject

@Composable
fun TimelineScreen(timelineViewModel: TimelineViewModel = koinInject()) {
    val posts by timelineViewModel.posts
    Posts(
        posts = posts,
        isLoadingNext = timelineViewModel.isLoadingNext,
        onReachBottomPost = timelineViewModel::onReachedBottomPost,
        onRefresh = timelineViewModel::onRefresh,
    )
}
