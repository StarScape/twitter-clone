package com.example.twitterclone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
) {
    data object Login : Screen("login")
    data object CreateAccount : Screen("create_account")

    abstract class AuthenticatedScreen(route: String, val icon: ImageVector, val resourceId: Int): Screen(route)
    data object Timeline : AuthenticatedScreen("timeline", Icons.Filled.List, R.string.timeline)
    data object NewPost: AuthenticatedScreen("newpost", Icons.Filled.Add, R.string.newpost)
    data object Profile: AuthenticatedScreen("profile", Icons.Filled.AccountCircle, R.string.profile)
    companion object {
        // List of screens visible after authentication
        val authenticatedScreens = listOf(Timeline, NewPost, Profile)
    }
}
