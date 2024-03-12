package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitterclone.data.User
import com.example.twitterclone.ui.Posts
import com.example.twitterclone.ui.viewmodels.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = koinInject()) {
    val userPosts = profileViewModel.userPosts.value
    Column {
        UserOverview(profileViewModel.user.value, onLogout = profileViewModel::onLogout)
        Posts(userPosts)
    }
}

@Composable
fun UserOverview(user: User?, onLogout: () -> Unit) {
    val username = user?.username ?: "Loading..."
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
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
                    text = { Text("Logout") }
                )
            }
        }
    )
}
