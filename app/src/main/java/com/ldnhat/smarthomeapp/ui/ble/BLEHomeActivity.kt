package com.ldnhat.smarthomeapp.ui.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.ui.auth.AuthActivity
import com.ldnhat.smarthomeapp.ui.home.HomeActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BLEHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_home)

        val userPreferences = UserPreferences(this)
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        userPreferences.accessToken.asLiveData().observe(this@BLEHomeActivity) {
                            val activity =
                                if (it == null) AuthActivity::class.java else HomeActivity::class.java
                            startNewActivity(activity)
                        }
                        startNewActivity(HomeActivity::class.java)
                    }
                }
            }
        })
    }
}