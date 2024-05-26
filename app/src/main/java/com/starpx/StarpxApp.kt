package com.starpx

import android.app.Application
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.starpx.utils.LifecycleHelper

class StarpxApp : Application() {
    lateinit var userPool: CognitoUserPool

    override fun onCreate() {
        super.onCreate()

        LifecycleHelper.getInstance().appContext = this

        try {
            val awsConfig = AWSConfiguration(applicationContext)
            userPool = CognitoUserPool(applicationContext, awsConfig)
            Log.d("StarpxApp", "Cognito User Pool initialized successfully")
        } catch (e: Exception) {
            Log.e("StarpxApp", "Error initializing Cognito User Pool", e)
        }

        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of the app's available memory for image caching
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(512L * 1024 * 1024) // 512 MB of disk cache
                    .build()
            }
            .build()

        Coil.setImageLoader(imageLoader)
    }
}