package com.starpx.views

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@Composable
fun GlideImage(
    url: String?,
    modifier: Modifier = Modifier,
    requestOptions: RequestOptions = RequestOptions()
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = modifier,
        update = { imageView ->
            Glide.with(imageView)
                .load(url)
                .apply(requestOptions)
                .into(imageView)
        }
    )
}