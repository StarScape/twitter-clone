package com.example.twitterclone.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twitterclone.Screen
import com.example.twitterclone.TwitterCloneNavigator
import com.example.twitterclone.data.Post
import com.example.twitterclone.data.PostRepository
import com.example.twitterclone.data.User
import com.example.twitterclone.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val navigator: TwitterCloneNavigator,
) : ViewModel() {
    val paginator = postRepository.getPaginator()
    val userPosts: MutableState<List<Post>> = mutableStateOf(mutableListOf())
    val user: MutableState<User?> = mutableStateOf(null)
    var isLoadingNext by mutableStateOf(true)

    init {
        viewModelScope.launch {
            userPosts.value = paginator.getNextN()
            isLoadingNext = false

            val userUid = auth.currentUser!!.uid // won't be null since this screen requires auth
            user.value = userRepository.getUser(userUid)
        }
    }

    fun onReachedBottomPost() {
        isLoadingNext = !paginator.endReached
        viewModelScope.launch {
            userPosts.value += paginator.getNextN()
            isLoadingNext = false
        }
    }

    suspend fun onRefresh() {
        isLoadingNext = false
        paginator.reset()
        userPosts.value = paginator.getNextN()
    }

    fun onLogout() {
        auth.signOut()
        navigator.navController.navigate(Screen.Login.route)
    }
}