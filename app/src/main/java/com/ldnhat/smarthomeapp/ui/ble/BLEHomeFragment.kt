package com.ldnhat.smarthomeapp.ui.ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.constants.CommonConstant
import com.ldnhat.smarthomeapp.common.constants.CommonConstant.Companion.BLUETOOTH_ALL_PERMISSIONS_REQUEST_CODE
import com.ldnhat.smarthomeapp.common.constants.CommonConstant.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.ldnhat.smarthomeapp.common.enumeration.AskType
import com.ldnhat.smarthomeapp.common.enumeration.BLELifecycleState
import com.ldnhat.smarthomeapp.databinding.FragmentBleHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class BLEHomeFragment : Fragment() {
    private val viewModel by viewModels<BLEHomeViewModel>()
    private var handlerAnimation = Handler(Looper.getMainLooper())
    private var statusAnimation = false

    private lateinit var binding: FragmentBleHomeBinding

    private var userWantsToScanAndConnect: Boolean = false

    private var isScanning = false
    private var connectedGatt: BluetoothGatt? = null
    private var characteristicForRead: BluetoothGattCharacteristic? = null
    private var characteristicForWrite: BluetoothGattCharacteristic? = null
    private var characteristicForIndicate: BluetoothGattCharacteristic? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble_home, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}