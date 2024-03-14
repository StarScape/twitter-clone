package com.example.twitterclone.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val newPostText by newPostViewModel.newPostText
        val currentPhotoUri by newPostViewModel.currentPhotoUri
        val isCurrentPhoto = currentPhotoUri.toString().isNotEmpty()

        AnimatedVisibility(visible = isCurrentPhoto) {
            Column {
                AsyncImage(
                    modifier = Modifier.size(300.dp)
                        .padding(horizontal = 30.dp, vertical = 10.dp),
                    model = currentPhotoUri,
                    contentDescription = null
                )
                Spacer(Modifier.height(30.dp))
            }
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
            colors = TextFieldDefaults.colors(),
            modifier = Modifier.fillMaxWidth(),
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
    val photoUri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onAddPhoto(photoUri)
            }
        }
    )

    val cameraPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.CAMERA,
        onPermissionResult = { granted ->
            if (granted) {
                cameraLauncher.launch(photoUri)
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

    if (!showOptions) {
        Button(
            onClick = { showOptions = true },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(if (showOptions) "X" else "+ Photo")
        }
    }

    if (showOptions) {
        Row {
            Button(
                onClick = {
                    showOptions = false
                },
                modifier = Modifier.padding(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Icon(Icons.Filled.Clear, "Clear")
            }

            val modifier = Modifier.padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
            Button(
                onClick = {
                    pickPhotoLauncher.launch("image/*")
                    showOptions = false
                },
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Filled.AttachFile,
                    contentDescription = "Select Photo",
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Select", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    showOptions = false
                },
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Select Photo"
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Take", fontSize = 12.sp)
            }
        }
    }
}

@Preview
@Composable
fun AddButtonPreview() {
    AddPhotoButton(onAddPhoto = {})
}
