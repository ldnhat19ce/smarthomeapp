package com.ldnhat.smarthomeapp

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.ui.auth.AuthActivity
import com.ldnhat.smarthomeapp.ui.ble.BLEHomeActivity
import com.ldnhat.smarthomeapp.ui.home.HomeActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)

//        val currentNetwork = connectivityManager.activeNetwork
//        if(null == currentNetwork) {
//            startNewActivity(BLEHomeActivity::class.java)
//        }
        startNewActivity(BLEHomeActivity::class.java)

//        val userPreferences = UserPreferences(this)
//        connectivityManager.registerDefaultNetworkCallback(object :
//            ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.Main) {
//                        userPreferences.accessToken.asLiveData().observe(this@MainActivity) {
//                            val activity =
//                                if (it == null) AuthActivity::class.java else HomeActivity::class.java
//                            startNewActivity(activity)
//                        }
//                        startNewActivity(HomeActivity::class.java)
//                    }
//                }
//            }
//
//            override fun onLost(network: Network) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.Main) {
//                        startNewActivity(BLEHomeActivity::class.java)
//                    }
//                }
//            }
//        })

    }
}