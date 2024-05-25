package com.starpx.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DialogManager {
    var showDialog: Boolean by mutableStateOf(false)
    var dialogMessage: String by mutableStateOf("Loading...")

    fun showProgressDialog(message: String = "Loading...") {
        dialogMessage = message
        showDialog = true
    }

    fun hideProgressDialog() {
        showDialog = false
    }
}