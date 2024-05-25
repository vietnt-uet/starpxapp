package com.starpx.viewmodel
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class DetailImageViewModel : ViewModel() {
    fun shareImage(context: Context, imageUrl: String) {
        viewModelScope.launch {
            val bitmap = fetchImageAsBitmap(context, imageUrl)
            bitmap?.let {
                shareImage(context, bitmap)
            }
        }
    }

    private suspend fun fetchImageAsBitmap(context: Context, imageUrl: String): Bitmap? {
        val imageLoader = Coil.imageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Important for manipulation and sharing
            .build()

        val result = imageLoader.execute(request)
        return if (result is SuccessResult) {
            (result.drawable as? BitmapDrawable)?.bitmap
        } else {
            null
        }
    }

    private fun shareImage(context: Context, bitmap: Bitmap) {
        val uri = getmageToShare(context, bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setType("image/png")
        context.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    // Retrieving the url to share
    private fun getmageToShare(context: Context, bitmap: Bitmap): Uri? {
        val imagefolder = File(context.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagefolder.mkdirs()
            val file = File(imagefolder, "shared_image_${Date().time}.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
        }
        return uri
    }
}