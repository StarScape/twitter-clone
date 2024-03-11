package com.example.twitterclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.twitterclone.ui.screens.CreateAccountScreen
import com.example.twitterclone.ui.screens.LoginScreen
import com.example.twitterclone.ui.screens.NewPostScreen
import com.example.twitterclone.ui.screens.ProfileScreen
import com.example.twitterclone.ui.screens.TimelineScreen
import com.example.twitterclone.ui.theme.TwitterCloneTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class TwitterCloneActivity : ComponentActivity() {
    private val auth: FirebaseAuth by inject()
    private lateinit var authManager: AuthManager
    private val navigator: TwitterCloneNavigator by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(auth, Firebase.firestore, this)

        setContent {
            val navController = rememberNavController()
            navigator.navController = navController

            TwitterCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(navController, authManager)
                }
            }
        }
    }
}

@Composable
fun Main(navController: NavHostController, authManager: AuthManager, modifier: Modifier = Modifier) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                Screen.authenticatedScreens.forEach { screen ->
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        // FirebaseAuth.getInstance().signOut() // for debugging
        val initialRoute = if (authManager.auth.currentUser != null) {
            Screen.Timeline.route
        } else {
            Screen.Login.route
        }
        NavHost(
            navController,
            startDestination = initialRoute,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController, authManager)
            }
            composable(Screen.CreateAccount.route) {
                CreateAccountScreen(navController, authManager)
            }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.NewPost.route) { NewPostScreen() }
            composable(Screen.Timeline.route) { TimelineScreen() }
        }
    }
}
