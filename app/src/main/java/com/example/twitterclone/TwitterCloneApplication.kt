package com.example.twitterclone

import android.app.Application
import com.example.twitterclone.data.PostRepository
import com.example.twitterclone.data.UserRepository
import com.example.twitterclone.ui.viewmodels.NewPostViewModel
import com.example.twitterclone.ui.viewmodels.ProfileViewModel
import com.example.twitterclone.ui.TimelineViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
    single { TwitterCloneNavigator() }
    single { PostRepository(db = get(), storage = get()) }
    single { UserRepository(db = get()) }

    // ViewModel instance created by Koin
    viewModel { TimelineViewModel(repository = get()) }
    viewModel { NewPostViewModel(repository = get(), navigator = get()) }
    viewModel {
        ProfileViewModel(
            postRepository = get(),
            userRepository = get(),
            auth = get(),
            navigator = get()
        )
    }
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