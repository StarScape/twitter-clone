package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.twitterclone.AuthManager
import com.example.twitterclone.Screen

@Composable
fun CreateAccountScreen(navController: NavController, authManager: AuthManager) {
    var password by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var accountCreationError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Create an account")

        // Email text field
        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
            },
            label = { Text("Email") }
        )

        // Username text field
        OutlinedTextField(
            value = username,
            onValueChange = { newValue ->
                username = newValue
            },
            label = { Text("Username") }
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

        // Password text field
        OutlinedTextField(
            value = confirmedPassword,
            onValueChange = { newValue ->
                confirmedPassword = newValue
            },
            label = { Text("Confirm password") },
            // Hide password characters
            visualTransformation = PasswordVisualTransformation(),
            // Optional: specify keyboard type as Password
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (password.isNotBlank() && confirmedPassword.isNotBlank() && password != confirmedPassword) {
            Text(text = "Passwords do not match.")
        }

        // Submit button
        Button(
            onClick = {
                // Pass username, password, and email to the callback
                authManager.createAccount(email, username, password) { e ->
                    if (e == null) {
                        navController.navigate(Screen.Login.route)
                    } else {
                        accountCreationError = "Failed to create account: " + e.localizedMessage
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Create Account")
        }

        if (accountCreationError.isNotBlank()) {
            Text(accountCreationError, color = Color.Red)
        }
    }
}