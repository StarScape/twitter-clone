package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.unit.dp
import com.example.twitterclone.data.User
import com.example.twitterclone.ui.Posts
import com.example.twitterclone.ui.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = koinInject()) {
    val userPosts = profileViewModel.userPosts.value
    Column {
        UserOverview(profileViewModel.user.value)
        Posts(userPosts)
    }
}

@Composable
fun UserOverview(user: User?) {
    val username = user?.username ?: "Loading..."
    Text(
        text = "Current user: ${username}",
        modifier = Modifier.padding(bottom = 10.dp)
    )
}
