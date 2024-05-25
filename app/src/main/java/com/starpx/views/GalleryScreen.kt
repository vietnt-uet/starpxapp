package com.starpx.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.starpx.DetailImageActivity
import com.starpx.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = viewModel()) {
    val imageSets by viewModel.imageSets.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
            items(imageSets) { imageSet ->
                ImageItem(imageUrl = imageSet.image_detail?.thumbs?.small) {
                    context.startActivity(Intent(context, DetailImageActivity::class.java).apply {
                        putExtra("image_url", imageSet.image_detail?.full_url)
                    })
                }
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    LaunchedEffect(Unit) {
                        if (viewModel.nextToken != null) {
                            isLoading = true
                            viewModel.fetchImageSets(viewModel.nextToken)
                            isLoading = false
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun GalleryDeviceImageScreen(images: ArrayList<Uri>) {
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
                        putExtra("image_url", imageUri.toString())
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

@Composable
fun ImageItem(imageUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
    ) {
        val imageModifier = Modifier
            .size(200.dp)
            .border(BorderStroke(1.dp, Color.Transparent))
            .background(Color.LightGray)

        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = "Image thumb",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }
    }
}