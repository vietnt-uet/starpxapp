package com.starpx.views

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.starpx.DetailImageActivity
import com.starpx.utils.KEY_IMAGE_FULL_URL
import com.starpx.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = viewModel()) {
    val imageSets by viewModel.imageSets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        modifier = Modifier.fillMaxSize(),
        content = {
            items(imageSets) { imageSet ->
                ImageItem(imageUrl = imageSet.image_detail?.thumbs?.small) {
                    context.startActivity(Intent(context, DetailImageActivity::class.java).apply {
                        putExtra(KEY_IMAGE_FULL_URL, imageSet.image_detail?.full_url)
                    })
                }
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    )

    // Launching side effects for pagination
    LaunchedEffect(imageSets.lastOrNull()) {
        if (!isLoading && viewModel.nextToken != null) {
            viewModel.fetchImageSets(viewModel.nextToken)
        }
    }
}