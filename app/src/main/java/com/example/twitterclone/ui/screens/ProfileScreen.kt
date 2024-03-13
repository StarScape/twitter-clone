package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.example.twitterclone.data.User
import com.example.twitterclone.ui.Posts
import com.example.twitterclone.ui.viewmodels.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = koinInject()) {
    val userPosts = profileViewModel.userPosts.value
    Column {
        UserOverview(profileViewModel.user.value, onLogout = profileViewModel::onLogout)
        Posts(
            posts = userPosts,
            isLoadingNext = profileViewModel.isLoadingNext,
            onReachBottomPost = profileViewModel::onReachedBottomPost,
            onRefresh = profileViewModel::onRefresh,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOverview(user: User?, onLogout: () -> Unit) {
    val username = user?.username?.let { name -> "${name}'s posts"} ?: "Loading..."
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier.zIndex(2.0f),
        title = { Text(username) },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Filled.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = { onLogout() },
                    text = { Text("Logout") },
                )
            }
        }
    )
}
