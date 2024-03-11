package com.example.twitterclone

import android.app.Application
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.twitterclone.data.PostRepository
import com.example.twitterclone.ui.NewPostViewModel
import com.example.twitterclone.ui.TimelineViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TwitterCloneNavigator {
    lateinit var navController: NavHostController
}

val appModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { PostRepository() }
    single { TwitterCloneNavigator() }

    // ViewModel instance created by Koin
    viewModel { TimelineViewModel(repository = get()) }
    viewModel { NewPostViewModel(repository = get(), navigator = get()) }
}

class TwitterCloneApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TwitterCloneApp)
            modules(appModule)
        }
    }
}