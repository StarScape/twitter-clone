package com.example.twitterclone.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.twitterclone.Screen
import com.example.twitterclone.TwitterCloneNavigator
import com.example.twitterclone.data.PostRepository

// TODO: Move to constants file
const val TWEET_CHAR_LIMIT = 150

class NewPostViewModel(
    private val repository: PostRepository,
    private val navigator: TwitterCloneNavigator,
) : ViewModel() {
    var newPostText by mutableStateOf("")
    var isValidPost by mutableStateOf(false)

    fun setPostText(newText: String) {
        newPostText = newText
        isValidPost = newText.length > TWEET_CHAR_LIMIT
    }

    fun tryPost() {
        if (newPostText.isNotBlank() && !isValidPost) {
            repository.createPost(newPostText)
            newPostText = ""
            navigator.navController.navigate(Screen.Timeline.route)
        }
    }
}
