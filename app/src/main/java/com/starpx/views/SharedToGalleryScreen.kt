package com.starpx.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.starpx.DetailImageActivity
import com.starpx.utils.KEY_IMAGE_FULL_URL

@Composable
fun SharedToGalleryScreen(images: ArrayList<Uri>) {
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
            items(images) { imageUri ->
                ImageItem(imageUrl = imageUri.toString()) {
                    context.startActivity(Intent(context, DetailImageActivity::class.java).apply {
                        putExtra(KEY_IMAGE_FULL_URL, imageUri.toString())
                    })
                }
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    )
}