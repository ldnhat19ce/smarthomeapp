package com.ldnhat.smarthomeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.ui.auth.AuthActivity
import com.ldnhat.smarthomeapp.ui.home.HomeActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userPreferences = UserPreferences(this)
        userPreferences.accessToken.asLiveData().observe(this) {
            val activity = if (it == null) AuthActivity::class.java else HomeActivity::class.java
            startNewActivity(activity)
        }
    }
}