package com.starpx

import android.app.Application
import android.util.Log
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool

class StarpxApp : Application() {
    lateinit var userPool: CognitoUserPool

    override fun onCreate() {
        super.onCreate()

        try {
            val awsConfig = AWSConfiguration(applicationContext)
            userPool = CognitoUserPool(applicationContext, awsConfig)
            Log.d("StarpxApp", "Cognito User Pool initialized successfully")
        } catch (e: Exception) {
            Log.e("StarpxApp", "Error initializing Cognito User Pool", e)
        }
    }
}