package com.starpx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.starpx.localstorage.PreferenceUtil
import com.starpx.utils.KEY_ACCESS_TOKEN
import com.starpx.viewmodel.GalleryViewModel
import com.starpx.views.GalleryScreen

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
            Text(stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.TwoTone.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        }
    )
}