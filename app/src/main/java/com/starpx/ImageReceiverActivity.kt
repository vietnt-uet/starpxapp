package com.starpx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.starpx.views.SharedToGalleryScreen

class ImageReceiverActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            androidx.compose.material.Text(stringResource(id = R.string.share_to_app))
                        },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back button",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    HandleIntentImages(intent)
                }
            }
        }
    }
}

@Composable
fun HandleIntentImages(intent: Intent?) {
    val imageUri: Uri? = intent?.getParcelableExtra(Intent.EXTRA_STREAM)
    if (imageUri != null && intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
        SingleImageDisplay(imageUrl = imageUri.toString())
    } else if (intent?.action == Intent.ACTION_SEND_MULTIPLE && intent.type?.startsWith("image/") == true) {
        val imageUris: ArrayList<Uri> =
            intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM) ?: arrayListOf()
        SharedToGalleryScreen(imageUris)
    }
}

@Composable
fun SingleImageDisplay(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = "Detailed Image",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}