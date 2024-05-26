package com.starpx

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import com.starpx.utils.PreferenceUtil
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
                    AlertDialog.Builder(this@LoginActivity).setTitle(getString(R.string.string_oops)).setMessage("Login failed: ${exception?.localizedMessage}").setNegativeButton(R.string.string_close) { dialog, _ ->
                        dialog.cancel()
                    }.show()
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
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.username)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions (
                onNext = { focusRequester.requestFocus() }
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onLogin(username, password) }
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onLogin(username, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotBlank() && password.isNotBlank()) {
            Text("Login")
        }
        ProgressDialog()
    }
}