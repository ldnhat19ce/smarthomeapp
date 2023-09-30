package com.ldnhat.smarthomeapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.ui.ble.BLEHomeActivity
import com.ldnhat.smarthomeapp.ui.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val RecordAudioRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        startNewActivity(BLEHomeActivity::class.java)
                    }
                }
            }
        })

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }
    }

    private fun checkPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            this.RecordAudioRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.RecordAudioRequestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}