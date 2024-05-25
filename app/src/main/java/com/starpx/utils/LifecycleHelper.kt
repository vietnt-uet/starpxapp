package com.starpx.utils

import com.starpx.StarpxApp

class LifecycleHelper {
    companion object {

        @Volatile
        private var instance: LifecycleHelper? = null

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: LifecycleHelper().also { instance = it }
            }
    }

    lateinit var appContext: StarpxApp
}