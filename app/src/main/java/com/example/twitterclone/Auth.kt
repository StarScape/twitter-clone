package com.example.twitterclone

import android.app.Activity
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val activity: Activity
) {
    fun createAccount(
        email: String,
        username: String,
        password: String, onRequestFinished: (Exception?) -> Unit
    ) {
        // Create user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Once user is created, add them to the "user"
                    // collection in the DB, which holds extra info
                    // such as username, profile pic, etc.
                    val uid = task.result.user!!.uid
                    val dbUser = mapOf(
                        "username" to username,
                    )
                    db.collection("users").document(uid)
                        .set(dbUser)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(
                                    activity.baseContext,
                                    R.string.account_created_successfully,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            onRequestFinished(updateTask.exception)
                        }
                } else {
                    onRequestFinished(task.exception)
                }
            }
    }

    fun signIn(email: String, password: String, onRequestFinished: (Task<AuthResult>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                onRequestFinished(task)
            }
    }
}