package com.example.twitterclone.ui.viewmodels

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.twitterclone.Screen
import com.example.twitterclone.TwitterCloneNavigator
import com.example.twitterclone.data.PostRepository

// TODO: Move to constants file
const val TWEET_CHAR_LIMIT = 150

class NewPostViewModel(
    private val repository: PostRepository,
    private val navigator: TwitterCloneNavigator,
) : ViewModel() {
    private var _newPostText = mutableStateOf("")
    val newPostText: State<String> = _newPostText

    private var _currentPhotoUri = mutableStateOf(value = Uri.EMPTY)
    val currentPhotoUri: State<Uri> = _currentPhotoUri

    var isValidPost by mutableStateOf(false)

    private fun checkValidPost(): Boolean {
        val includesPhoto = _currentPhotoUri.toString().isNotEmpty()
        val isWithinCharLimit = newPostText.value.length <= TWEET_CHAR_LIMIT
        return (includesPhoto && isWithinCharLimit)
                || (isWithinCharLimit && newPostText.value.isNotEmpty())
    }

    /**
     * Setter for post text. The UI cannot set post text directly
     * because [[isValidPost]] needs to be kept in sync with it.
     */
    fun onUpdatePostText(newText: String) {
        _newPostText.value = newText
        isValidPost = checkValidPost()
    }

    /**
     * Setter for photo URI. Similar to post text, the UI cannot set it
     * directly since [[isValidPost]] needs to be kept in sync with it.
     */
    fun setCurrentPhotoUri(newUri: Uri) {
        _currentPhotoUri.value = newUri
        isValidPost = checkValidPost()
    }

    fun tryPost() {
        val postText = _newPostText.value
        if (postText.isNotBlank() && !isValidPost) {
            repository.createPost(postText)
            _newPostText.value = ""
            navigator.navController.navigate(Screen.Timeline.route)
        }
    }
}
