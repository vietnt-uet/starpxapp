package com.starpx.localstorage

import android.content.Context
import android.content.SharedPreferences
import com.starpx.utils.LifecycleHelper

class PreferenceUtil private constructor(context: Context) {

    lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val FILE_NAME = "secure_prefs"

        @Volatile
        private var instance: PreferenceUtil? = null

        @JvmStatic
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PreferenceUtil(context).also {
                    instance = it
                    instance?.sharedPreferences =
                        LifecycleHelper.getInstance().appContext.getSharedPreferences(
                            FILE_NAME, Context.MODE_PRIVATE
                        )
                }
            }
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun setValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun setValue(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun setValue(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun removeValueOfKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun removeAll() {
        sharedPreferences.edit().clear().apply()
    }

}