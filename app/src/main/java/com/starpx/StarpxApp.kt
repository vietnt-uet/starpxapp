package com.starpx

import android.app.Application
import android.util.Log
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
    }
}