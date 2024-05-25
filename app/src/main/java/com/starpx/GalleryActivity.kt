package com.starpx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.starpx.localstorage.PreferenceUtil
import com.starpx.utils.KEY_ACCESS_TOKEN
import com.starpx.viewmodel.GalleryViewModel

class GalleryActivity : ComponentActivity() {
    private lateinit var userPool: CognitoUserPool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPool = (application as StarpxApp).userPool

        setContent {
            ScaffoldGallery { logout(this@GalleryActivity) }
        }
    }

    private fun logout(context: Context) {
        //Clear SharedPreference and logout AWS Cognito then open login screen again.

        PreferenceUtil.getInstance(context).removeAll()

        val userPool = (context.applicationContext as StarpxApp).userPool
        val currentUser = userPool.currentUser
        currentUser.signOut()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}

@Composable
fun ScaffoldGallery(onLogOut: (context: Context) -> Unit) {
    val context = LocalContext.current
    val accessToken = PreferenceUtil.getInstance(context).getString(
        KEY_ACCESS_TOKEN
    )

    val galleryViewModel = GalleryViewModel()
    galleryViewModel.initializeApolloClient(accessToken ?: "")

    Scaffold(
        topBar = {
            GalleryAppBar { onLogOut(context) }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GalleryScreen(galleryViewModel)
        }
    }
}

@Composable
fun GalleryAppBar(onMenuClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text("Starpx")
        },
        actions = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.TwoTone.ExitToApp,
                    contentDescription = "Localized description",
                    tint = Color.White
                )
            }
        }
    )
}

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
            )
        }
    }
}