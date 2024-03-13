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
    var isLoadingNext by mutableStateOf(true)

    init {
        viewModelScope.launch {
            posts.value = paginator.getNextN()
            isLoadingNext = false
        }
    }

    fun onReachedBottomPost() {
        isLoadingNext = !paginator.endReached
        viewModelScope.launch {
            posts.value += paginator.getNextN()
            isLoadingNext = false
        }
    }

    suspend fun onRefresh() {
        isLoadingNext = false
        paginator.reset()
        posts.value = paginator.getNextN()
    }
}