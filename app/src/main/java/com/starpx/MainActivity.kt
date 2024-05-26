package com.starpx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.starpx.utils.KEY_ACCESS_TOKEN
import com.starpx.utils.PreferenceUtil

class MainActivity : ComponentActivity() {
    private lateinit var userPool: CognitoUserPool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPool = (application as StarpxApp).userPool

        checkUserSession()
    }

    private fun checkUserSession() {
        val currentUser = userPool.currentUser
        currentUser.getSessionInBackground(object : com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {

                //Update new access_token every time user open app again.
                PreferenceUtil.getInstance(this@MainActivity).setValue(KEY_ACCESS_TOKEN,  userSession?.accessToken?.jwtToken ?: "")

                if (userSession != null && userSession.isValid) {
                    navigateToGallery()
                } else {
                    navigateToLogin()
                }
            }

            override fun getAuthenticationDetails(authenticationContinuation: com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation?, userId: String?) {
                Toast.makeText(this@MainActivity, "getAuthenticationDetails", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }

            override fun getMFACode(multiFactorAuthenticationContinuation: com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation?) {
                Toast.makeText(this@MainActivity, "getMFACode", Toast.LENGTH_SHORT).show()
            }

            override fun authenticationChallenge(continuation: com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation?) {
                Toast.makeText(this@MainActivity, "authenticationChallenge", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(exception: Exception?) {
                navigateToLogin()
            }
        })
    }

    private fun navigateToGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}