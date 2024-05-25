package com.starpx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.starpx.viewmodel.DetailImageViewModel
import com.starpx.views.ProgressDialog
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable


class DetailImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUrl = intent.getStringExtra("image_url") ?: ""

        setContentView(ComposeView(this).apply {
            setContent {
                setContent {
                    DetailScreen(imageUrl)
                }
            }
        })
    }

    @Composable
    fun DetailScreen(imageUrl: String, viewModel: DetailImageViewModel = viewModel()) {
        val context = LocalContext.current
        val zoomState = rememberZoomState()

        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = {
                    finish()
                },
                Modifier.zIndex(9f),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }


            SubcomposeAsyncImage(
                model = imageUrl,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                contentDescription = "Detail image full size",
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState),
            )

            FloatingActionButton(
                onClick = { viewModel.shareImage(context, imageUrl) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Share")
            }

            ProgressDialog()
        }
    }
}