package com.example.twitterclone.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import com.example.twitterclone.data.User
import com.example.twitterclone.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val userPosts: MutableState<List<Post>> = mutableStateOf(emptyList())
    val user: MutableState<User?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            userPosts.value = postRepository.getPosts(Firebase.auth.currentUser?.uid)

            val userUid = auth.currentUser!!.uid // won't be null since this screen requires auth
            user.value = userRepository.getUser(userUid)
        }
    }
}