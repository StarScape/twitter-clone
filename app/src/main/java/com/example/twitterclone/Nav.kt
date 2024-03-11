package com.example.twitterclone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

/**
 * Represents the difference screens in the app.
 */
sealed class Screen(
    val route: String,
) {
    data object Login : Screen("login")
    data object CreateAccount : Screen("create_account")

    // Screens that appear as a bottom on the app's bottom nav
    abstract class BottomNavScreen(route: String, val icon: ImageVector, val resourceId: Int): Screen(route)
    data object Timeline : BottomNavScreen("timeline", Icons.Filled.List, R.string.timeline)
    data object NewPost: BottomNavScreen("newpost", Icons.Filled.Add, R.string.newpost)
    data object Profile: BottomNavScreen("profile", Icons.Filled.AccountCircle, R.string.profile)
    companion object {
        // List of screens visible after authentication
        val authenticatedScreens = listOf(Timeline, NewPost, Profile)
    }
}

/**
 * This class is a workaround for being able to inject the nav controller into
 * classes. It is just a container object that holds a `lateinit`'d reference to
 * the nav controller (set early in the application lifecycle, in [[TwitterCloneActivity]].
 *
 * There might be a more elegant way to do this (there is in Hilt), but this is fine
 * as a workaround.
 */
class TwitterCloneNavigator {
    lateinit var navController: NavHostController
}
