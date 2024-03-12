package com.example.twitterclone.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import kotlinx.coroutines.launch

class TimelineViewModel(private val repository: PostRepository) : ViewModel() {
    val paginator = repository.getPaginator()
    val posts: MutableState<List<Post>> = mutableStateOf(emptyList())
    var isLoadingPosts by mutableStateOf(true)

    init {
        viewModelScope.launch {
            posts.value = paginator.getNextN()
            isLoadingPosts = false
        }
    }

    fun onReachedBottomPost() {
        isLoadingPosts = !paginator.endReached
        viewModelScope.launch {
            posts.value += paginator.getNextN()
            isLoadingPosts = false
        }
    }
}