package com.example.twitterclone.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.twitterclone.BuildConfig
import com.example.twitterclone.createImageFile
import com.example.twitterclone.ui.viewmodels.NewPostViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.compose.koinInject
import java.util.Objects

@Composable
fun NewPostScreen(newPostViewModel: NewPostViewModel = koinInject()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val newPostText by newPostViewModel.newPostText
        val currentPhotoUri by newPostViewModel.currentPhotoUri
        val isCurrentPhoto = currentPhotoUri.toString().isNotEmpty()

        AnimatedVisibility(visible = isCurrentPhoto) {
            // from coil library
            AsyncImage(
                modifier = Modifier.size(size = 240.dp),
                model = currentPhotoUri,
                contentDescription = null
            )
        }

        val isValidPost = newPostViewModel.isValidPost
        val valid = (isCurrentPhoto && isValidPost)
                // don't display error when text box is empty
                || (newPostText.isEmpty() || isValidPost)
        TextField(
            value = newPostText,
            isError = !valid,
            onValueChange = newPostViewModel::onUpdatePostText,
            placeholder = {
                Text(text = if (isCurrentPhoto) "Share a thought (optional)..." else "Share a thought...")
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    newPostViewModel.tryPost()
                }
            ),
            minLines = 3,
            maxLines = 7,
            textStyle = MaterialTheme.typography.bodySmall,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surface
            ),
        )
        AddPhotoButton(
            onAddPhoto = { imageFileUri ->
                newPostViewModel.setCurrentPhotoUri(imageFileUri)
            }
        )

        Button(
            enabled = newPostViewModel.isValidPost,
            onClick = {
                newPostViewModel.tryPost()
            },
        ) {
            Text("Submit")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddPhotoButton(onAddPhoto: (Uri) -> Unit) {
    var showOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onAddPhoto(uri)
            }
        }
    )

    val cameraPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.CAMERA,
        onPermissionResult = { granted ->
            if (granted) {
                cameraLauncher.launch(uri)
            } else print("camera permission is denied")
        }
    )

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onAddPhoto(uri)
        }
    }

    Row {
        Button(
            onClick = { showOptions = !showOptions },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(if (showOptions) "X" else "+ Photo")
        }

        if (showOptions) {
            Row {
                Button(
                    onClick = {
                        pickPhotoLauncher.launch("image/*")
                        showOptions = false
                    },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Select")
                }

                Button(
                    onClick = cameraPermissionState::launchPermissionRequest,
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Take")
                }
            }
        }
    }
}
