package com.example.twitterclone.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.example.twitterclone.ui.NewPostViewModel
import org.koin.compose.koinInject

@Composable
fun NewPostScreen(newPostViewModel: NewPostViewModel = koinInject()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = newPostViewModel.newPostText,
            isError = newPostViewModel.isValidPost,
            onValueChange = { newPostViewModel.setPostText(it) },
            placeholder = { Text(text = "Share a thought...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    newPostViewModel.tryPost()
                }
            ),
            minLines = 7,
            maxLines = 10,
            textStyle = MaterialTheme.typography.body1,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
        )

        Button(
            enabled = !newPostViewModel.isValidPost,
            onClick = {
                newPostViewModel.tryPost()
            },
        ) {
            Text("Submit")
        }
    }
}
