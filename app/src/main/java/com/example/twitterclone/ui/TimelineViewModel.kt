package com.example.twitterclone.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimelineViewModel(private val repository: PostRepository) : ViewModel() {
    val posts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            posts.value = repository.getPosts()
        }
    }
}
