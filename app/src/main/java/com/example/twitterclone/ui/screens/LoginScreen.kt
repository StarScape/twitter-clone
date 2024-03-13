package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.twitterclone.AuthManager
import com.example.twitterclone.Screen

@Composable
fun LoginScreen(navController: NavController, authManager: AuthManager) {
    // Remember the current values of username and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Please Sign In")

        // Username text field
        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
            },
            label = { Text("Email") }
        )

        // Password text field
        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
            },
            label = { Text("Password") },
            // Hide password characters
            visualTransformation = PasswordVisualTransformation(),
            // Optional: specify keyboard type as Password
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Row(modifier = Modifier.padding(top = 16.dp)) {
            // Login button
            Button(
                onClick = {
                    authManager.signIn(email, password) { task ->
                        if (task.isSuccessful) {
                            navController.navigate(Screen.Timeline.route)
                        } else {
                            error = "Failed login: " + task.exception?.localizedMessage
                        }
                    }
                },
            ) {
                Text("Login")
            }
            // Create account button
            TextButton(
                onClick = {
                    navController.navigate(Screen.CreateAccount.route)
                },
            ) {
                Text("Create Account")
            }
        }
    }
}