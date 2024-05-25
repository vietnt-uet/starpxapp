package com.starpx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.starpx.localstorage.PreferenceUtil
import com.starpx.utils.KEY_ACCESS_TOKEN
import com.starpx.utils.KEY_CUSTOMER_ID
import com.starpx.views.DialogManager
import com.starpx.views.ProgressDialog

class LoginActivity : ComponentActivity() {
    private lateinit var userPool: CognitoUserPool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPool = (application as StarpxApp).userPool

        setContent {
            LoginScreen { username, password ->
                loginUser(username, password)
            }
        }
    }

    private fun navigateToGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginUser(username: String, password: String) {
        DialogManager.showProgressDialog("Signing in...")
        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                runOnUiThread {
                    PreferenceUtil.getInstance(this@LoginActivity).setValue(KEY_ACCESS_TOKEN,  userSession?.accessToken?.jwtToken ?: "")
                    //TODO: Temporary use fixed customerId
                    PreferenceUtil.getInstance(this@LoginActivity).setValue(KEY_CUSTOMER_ID,  "aabb1234")

                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToGallery()
                    DialogManager.hideProgressDialog()
                }
            }

            override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                val authenticationDetails = AuthenticationDetails(userId, password, null)
                authenticationContinuation?.setAuthenticationDetails(authenticationDetails)
                authenticationContinuation?.continueTask()
            }

            override fun getMFACode(multiFactorAuthenticationContinuation: MultiFactorAuthenticationContinuation?) {
                // Handle MFA if needed
                runOnUiThread {
                    DialogManager.hideProgressDialog()
                }
            }

            override fun onFailure(exception: Exception?) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    DialogManager.hideProgressDialog()
                }
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                runOnUiThread {
                    DialogManager.hideProgressDialog()
                }
            }
        }

        userPool.getUser(username).getSessionInBackground(authenticationHandler)
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var username by remember { mutableStateOf("hendrik@starpx.com") }
    var password by remember { mutableStateOf("StarpxStarpx1!") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogin(username, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Login")
        }
        ProgressDialog()
    }
}