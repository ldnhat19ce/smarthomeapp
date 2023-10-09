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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.constants.CommonConstant
import com.ldnhat.smarthomeapp.common.enumeration.AskType
import com.ldnhat.smarthomeapp.common.enumeration.BLELifecycleState
import com.ldnhat.smarthomeapp.common.extensions.BluetoothGATTExtension
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.databinding.FragmentBleHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BLEHomeActivity : AppCompatActivity() {
    private val switchConnect: Button
        get() = findViewById(R.id.buttonConnect)
    private val textViewLifecycleState: TextView
        get() = findViewById(R.id.textViewLifecycleState)
    private val textViewReadValue: TextView
        get() = findViewById(R.id.textViewReadValue)
    private val editTextWriteValue: TextView
        get() = findViewById<EditText>(R.id.editTextWriteValue)
    private val textViewIndicateValue: TextView
        get() = findViewById(R.id.textViewIndicateValue)
    private val textViewSubscription: TextView
        get() = findViewById(R.id.textViewSubscription)
    private val textViewLog: TextView
        get() = findViewById(R.id.textViewLog)
    private val scrollViewLog: ScrollView
        get() = findViewById(R.id.scrollViewLog)

    private val imgAnimation1: ImageView
        get() = findViewById(R.id.imgAnimation1)

    private val imgAnimation2: ImageView
        get() = findViewById(R.id.imgAnimation2)

    private var handlerAnimation = Handler(Looper.getMainLooper())
    private var statusAnimation = false

//    private val userWantsToScanAndConnect: Boolean get() = switchConnect.isChecked
    private var userWantsToScanAndConnect: Boolean = false
    private var isScanning = false
    private var connectedGatt: BluetoothGatt? = null
    private var characteristicForRead: BluetoothGattCharacteristic? = null
    private var characteristicForWrite: BluetoothGattCharacteristic? = null
    private var characteristicForIndicate: BluetoothGattCharacteristic? = null

    private val bluetoothExtension: BluetoothGATTExtension = BluetoothGATTExtension()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings: ScanSettings
        get() {
            return scanSettingsSinceM
        }

    private val scanFilter = ScanFilter.Builder()
        .setServiceUuid(ParcelUuid(UUID.fromString(CommonConstant.SERVICE_UUID)))
        .build()

    private var activityResultHandlers = mutableMapOf<Int, (Int) -> Unit>()
    private var permissionResultHandlers =
        mutableMapOf<Int, (Array<out String>, IntArray) -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_home)

//        val userPreferences = UserPreferences(this)
//        val connectivityManager = getSystemService(ConnectivityManager::class.java)
//        connectivityManager.registerDefaultNetworkCallback(object :
//            ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.Main) {
//                        userPreferences.accessToken.asLiveData().observe(this@BLEHomeActivity) {
//                            val activity =
//                                if (it == null) AuthActivity::class.java else HomeActivity::class.java
//                            startNewActivity(activity)
//                        }
//                        startNewActivity(HomeActivity::class.java)
//                    }
//                }
//            }
//        })

//        switchConnect.setOnCheckedChangeListener { _, isChecked ->
//            when (isChecked) {
//                true -> {
//                    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
//                    registerReceiver(bleOnOffListener, filter)
//                }
//                false -> {
//                    unregisterReceiver(bleOnOffListener)
//                }
//            }
//            bleRestartLifecycle()
//        }
        onClickScan()
        AppUtils.appendLog("MainActivity.onCreate", this, textViewLog, scrollViewLog)
    }

    private var lifecycleState = BLELifecycleState.Disconnected
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            AppUtils.appendLog("status = $value", this, textViewLog, scrollViewLog)
            runOnUiThread {
                textViewLifecycleState.text = "State: ${value.name}"
                if (value != BLELifecycleState.Connected) {
                    textViewSubscription.text = getString(R.string.text_not_subscribed)
                }
            }
        }

    override fun onDestroy() {
        bleEndLifecycle()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    fun onTapRead(view: View) {
        val gatt = connectedGatt ?: run {
            AppUtils.appendLog(
                "ERROR: read failed, no connected device",
                this,
                textViewLog,
                scrollViewLog
            )
            return
        }
        val characteristic = characteristicForRead ?: run {
            AppUtils.appendLog(
                "ERROR: read failed, characteristic unavailable ${CommonConstant.CHAR_FOR_READ_UUID}",
                this,
                textViewLog,
                scrollViewLog
            )
            return
        }
        with(bluetoothExtension) {
            if (!characteristic.isReadable()) {
                AppUtils.appendLog(
                    "ERROR: read failed, characteristic not readable ${CommonConstant.CHAR_FOR_READ_UUID}",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                return
            }
        }
        gatt.readCharacteristic(characteristic)
    }

    @SuppressLint("MissingPermission")
    fun onTapWrite(view: View) {
        val gatt = connectedGatt ?: run {
            AppUtils.appendLog(
                "ERROR: write failed, no connected device",
                this,
                textViewLog,
                scrollViewLog
            )
            return
        }
        val characteristic = characteristicForWrite ?: run {
            AppUtils.appendLog(
                "ERROR: write failed, characteristic unavailable ${CommonConstant.CHAR_FOR_WRITE_UUID}",
                this,
                textViewLog,
                scrollViewLog
            )
            return
        }
        with(bluetoothExtension) {
            if (!characteristic.isWriteable()) {
                AppUtils.appendLog(
                    "ERROR: write failed, characteristic not writeable ${CommonConstant.CHAR_FOR_WRITE_UUID}",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                return
            }
        }
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        characteristic.value = editTextWriteValue.text.toString().toByteArray(Charsets.UTF_8)
        gatt.writeCharacteristic(characteristic)
    }

    @SuppressLint("SetTextI18n")
    fun onTapClearLog(view: View) {
        textViewLog.text = "Logs:"
        AppUtils.appendLog("log cleared", this, textViewLog, scrollViewLog)
    }

    @SuppressLint("MissingPermission")
    private fun bleEndLifecycle() {
        safeStopBleScan()
        connectedGatt?.close()
        setConnectedGattToNull()
        lifecycleState = BLELifecycleState.Disconnected
    }

    private fun setConnectedGattToNull() {
        connectedGatt = null
        characteristicForRead = null
        characteristicForWrite = null
        characteristicForIndicate = null
    }

    @SuppressLint("MissingPermission")
    private fun bleRestartLifecycle() {
        runOnUiThread {
            if (userWantsToScanAndConnect) {
                if (connectedGatt == null) {
                    prepareAndStartBleScan()
                } else {
                    connectedGatt?.disconnect()
                }
            } else {
                bleEndLifecycle()
            }
        }
    }

    private fun prepareAndStartBleScan() {
        ensureBluetoothCanBeUsed { isSuccess, message ->
            AppUtils.appendLog(message, this, textViewLog, scrollViewLog)
            if (isSuccess) {
                safeStartBleScan()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun safeStartBleScan() {
        if (isScanning) {
            AppUtils.appendLog("Already scanning", this, textViewLog, scrollViewLog)
            return
        }

        val serviceFilter = scanFilter.serviceUuid?.uuid.toString()
        AppUtils.appendLog(
            "Starting BLE scan, filter: $serviceFilter",
            this,
            textViewLog,
            scrollViewLog
        )

        isScanning = true
        lifecycleState = BLELifecycleState.Scanning
        bleScanner.startScan(mutableListOf(scanFilter), scanSettings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun safeStopBleScan() {
        if (!isScanning) {
            AppUtils.appendLog("Already stopped", this, textViewLog, scrollViewLog)
            return
        }

        AppUtils.appendLog("Stopping BLE scan", this, textViewLog, scrollViewLog)
        isScanning = false
        bleScanner.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToIndications(
        characteristic: BluetoothGattCharacteristic,
        gatt: BluetoothGatt
    ) {
        val cccdUuid = UUID.fromString(CommonConstant.CCC_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccDescriptor ->
            if (!gatt.setCharacteristicNotification(characteristic, true)) {
                AppUtils.appendLog(
                    "ERROR: setNotification(true) failed for ${characteristic.uuid}",
                    this,
                    textViewLog,
                    scrollViewLog
                )
                return
            }
            cccDescriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            gatt.writeDescriptor(cccDescriptor)
        }
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    //region BLE Scanning

    private val scanSettingsSinceM = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
        .setReportDelay(0)
        .build()

    @SuppressLint("MissingPermission")
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val name: String? = result.scanRecord?.deviceName ?: result.device.name
            AppUtils.appendLog(
                "onScanResult name=$name address= ${result.device?.address}",
                this@BLEHomeActivity,
                textViewLog,
                scrollViewLog
            )
            safeStopBleScan()
            lifecycleState = BLELifecycleState.Connecting

            result.device.connectGatt(this@BLEHomeActivity, false, gattCallback)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            AppUtils.appendLog(
                "onBatchScanResults, ignoring",
                this@BLEHomeActivity,
                textViewLog,
                scrollViewLog
            )
        }

        override fun onScanFailed(errorCode: Int) {
            AppUtils.appendLog(
                "onScanFailed errorCode=$errorCode",
                this@BLEHomeActivity,
                textViewLog,
                scrollViewLog
            )
            safeStopBleScan()
            lifecycleState = BLELifecycleState.Disconnected
            bleRestartLifecycle()
        }
    }
    //endregion

    //region BLE events, when connected
    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            // TODO: timeout timer: if this callback not called - disconnect(), wait 120ms, close()

            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    AppUtils.appendLog(
                        "Connected to $deviceAddress",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )

                    // TODO: bonding state

                    // recommended on UI thread https://punchthrough.com/android-ble-guide/
                    Handler(Looper.getMainLooper()).post {
                        lifecycleState = BLELifecycleState.ConnectedDiscovering
                        gatt.discoverServices()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    AppUtils.appendLog(
                        "Disconnected from $deviceAddress",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )
                    setConnectedGattToNull()
                    gatt.close()
                    lifecycleState = BLELifecycleState.Disconnected
                    bleRestartLifecycle()
                }
            } else {
                // TODO: random error 133 - close() and try reconnect

                AppUtils.appendLog(
                    "ERROR: onConnectionStateChange status=$status deviceAddress=$deviceAddress, disconnecting",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )

                setConnectedGattToNull()
                gatt.close()
                lifecycleState = BLELifecycleState.Disconnected
                bleRestartLifecycle()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            AppUtils.appendLog(
                "onServicesDiscovered services.count=${gatt.services.size} status=$status",
                this@BLEHomeActivity,
                textViewLog,
                scrollViewLog
            )

            if (status == 129 /*GATT_INTERNAL_ERROR*/) {
                // it should be a rare case, this article recommends to disconnect:
                // https://medium.com/@martijn.van.welie/making-android-ble-work-part-2-47a3cdaade07
                AppUtils.appendLog(
                    "ERROR: status=129 (GATT_INTERNAL_ERROR), disconnecting",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                gatt.disconnect()

                startScan()
                statusAnimation = true

                return
            }

            val service = gatt.getService(UUID.fromString(CommonConstant.SERVICE_UUID)) ?: run {
                AppUtils.appendLog(
                    "ERROR: Service not found ${CommonConstant.SERVICE_UUID}, disconnecting",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                gatt.disconnect()
                return
            }

            connectedGatt = gatt
            characteristicForRead =
                service.getCharacteristic(UUID.fromString(CommonConstant.CHAR_FOR_READ_UUID))
            characteristicForWrite =
                service.getCharacteristic(UUID.fromString(CommonConstant.CHAR_FOR_WRITE_UUID))
            characteristicForIndicate =
                service.getCharacteristic(UUID.fromString(CommonConstant.CHAR_FOR_INDICATE_UUID))

            characteristicForIndicate?.let {
                lifecycleState = BLELifecycleState.ConnectedSubscribing
                subscribeToIndications(it, gatt)
            } ?: run {
                AppUtils.appendLog(
                    "WARN: characteristic not found ${CommonConstant.CHAR_FOR_INDICATE_UUID}",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                lifecycleState = BLELifecycleState.Connected
            }
        }

        @Suppress("DEPRECATION")
        @Deprecated("Used natively in Android 12 and lower")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristic.uuid == UUID.fromString(CommonConstant.CHAR_FOR_READ_UUID)) {
                val strValue = characteristic.value.toString(Charsets.UTF_8)
                val log = "onCharacteristicRead " + when (status) {
                    BluetoothGatt.GATT_SUCCESS -> "OK, value=\"$strValue\""
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> "not allowed"
                    else -> "error $status"
                }
                AppUtils.appendLog(log, this@BLEHomeActivity, textViewLog, scrollViewLog)
                runOnUiThread {
                    textViewReadValue.text = strValue
                }
            } else {
                AppUtils.appendLog(
                    "onCharacteristicRead unknown uuid $characteristic.uuid",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristic.uuid == UUID.fromString(CommonConstant.CHAR_FOR_WRITE_UUID)) {
                val log: String = "onCharacteristicWrite " + when (status) {
                    BluetoothGatt.GATT_SUCCESS -> "OK"
                    BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> "not allowed"
                    BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> "invalid length"
                    else -> "error $status"
                }
                AppUtils.appendLog(log, this@BLEHomeActivity, textViewLog, scrollViewLog)
            } else {
                AppUtils.appendLog(
                    "onCharacteristicWrite unknown uuid $characteristic.uuid",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
            }
        }

        @Suppress("DEPRECATION")
        @Deprecated("Used natively in Android 12 and lower")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == UUID.fromString(CommonConstant.CHAR_FOR_INDICATE_UUID)) {
                val strValue = characteristic.value.toString(Charsets.UTF_8)
                AppUtils.appendLog(
                    "onCharacteristicChanged value=\"$strValue\"",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
                runOnUiThread {
                    textViewIndicateValue.text = strValue
                }
            } else {
                AppUtils.appendLog(
                    "onCharacteristicChanged unknown uuid $characteristic.uuid",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (descriptor.characteristic.uuid == UUID.fromString(CommonConstant.CHAR_FOR_INDICATE_UUID)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val value = descriptor.value
                    val isSubscribed = value.isNotEmpty() && value[0].toInt() != 0
                    val subscriptionText = when (isSubscribed) {
                        true -> getString(R.string.text_subscribed)
                        false -> getString(R.string.text_not_subscribed)
                    }
                    AppUtils.appendLog(
                        "onDescriptorWrite $subscriptionText",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )
                    runOnUiThread {
                        textViewSubscription.text = subscriptionText
                    }
                } else {
                    AppUtils.appendLog(
                        "ERROR: onDescriptorWrite status=$status uuid=${descriptor.uuid} char=${descriptor.characteristic.uuid}",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )
                }

                // subscription processed, consider connection is ready for use
                lifecycleState = BLELifecycleState.Connected
            } else {
                AppUtils.appendLog(
                    "onDescriptorWrite unknown uuid $descriptor.characteristic.uuid",
                    this@BLEHomeActivity,
                    textViewLog,
                    scrollViewLog
                )
            }
        }
    }
    //endregion

    private var bleOnOffListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                BluetoothAdapter.STATE_ON -> {
                    AppUtils.appendLog(
                        "onReceive: Bluetooth ON",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )
                    if (lifecycleState == BLELifecycleState.Disconnected) {
                        bleRestartLifecycle()
                    }
                }
                BluetoothAdapter.STATE_OFF -> {
                    AppUtils.appendLog(
                        "onReceive: Bluetooth OFF",
                        this@BLEHomeActivity,
                        textViewLog,
                        scrollViewLog
                    )
                    bleEndLifecycle()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultHandlers[requestCode]?.let { handler ->
            handler(resultCode)
        } ?: runOnUiThread {
            AppUtils.appendLog(
                "ERROR: onActivityResult requestCode=$requestCode result=$resultCode not handled",
                this,
                textViewLog,
                scrollViewLog
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResultHandlers[requestCode]?.let { handler ->
            handler(permissions, grantResults)
        } ?: runOnUiThread {
            AppUtils.appendLog(
                "ERROR: onRequestPermissionsResult requestCode=$requestCode not handled",
                this,
                textViewLog,
                scrollViewLog
            )
        }
    }

    private fun ensureBluetoothCanBeUsed(completion: (Boolean, String) -> Unit) {
        grantBluetoothCentralPermissions(AskType.AskOnce) { isGranted ->
            if (!isGranted) {
                completion(false, "Bluetooth permissions denied")
                return@grantBluetoothCentralPermissions
            }

            enableBluetooth(AskType.AskOnce) { isEnabled ->
                if (!isEnabled) {
                    completion(false, "Bluetooth OFF")
                    return@enableBluetooth
                }

                grantLocationPermissionIfRequired(AskType.AskOnce) { isGranted ->
                    if (!isGranted) {
                        completion(false, "Location permission denied")
                        return@grantLocationPermissionIfRequired
                    }

                    completion(true, "Bluetooth ON, permissions OK, ready")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableBluetooth(askType: AskType, completion: (Boolean) -> Unit) {
        if (bluetoothAdapter.isEnabled) {
            completion(true)
        } else {
            val intentString = BluetoothAdapter.ACTION_REQUEST_ENABLE
            val requestCode = CommonConstant.ENABLE_BLUETOOTH_REQUEST_CODE

            // set activity result handler
            activityResultHandlers[requestCode] = { result ->
                Unit
                val isSuccess = result == Activity.RESULT_OK
                if (isSuccess || askType != AskType.InsistUntilSuccess) {
                    activityResultHandlers.remove(requestCode)
                    completion(isSuccess)
                } else {
                    // start activity for the request again
                    startActivityForResult(Intent(intentString), requestCode)
                }
            }

            // start activity for the request
            startActivityForResult(Intent(intentString), requestCode)
        }
    }

    private fun grantLocationPermissionIfRequired(askType: AskType, completion: (Boolean) -> Unit) {
        val wantedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // BLUETOOTH_SCAN permission has flag "neverForLocation", so location not needed
            completion(true)
        } else if (hasPermissions(wantedPermissions)) {
            completion(true)
        } else {
            runOnUiThread {
                val requestCode = CommonConstant.LOCATION_PERMISSION_REQUEST_CODE

                // prepare motivation message
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Location permission required")
                builder.setMessage("BLE advertising requires location access, starting from Android 6.0")
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissionArray(wantedPermissions, requestCode)
                }
                builder.setCancelable(false)

                // set permission result handler
                permissionResultHandlers[requestCode] = { permissions, grantResults ->
                    val isSuccess = grantResults.firstOrNull() != PackageManager.PERMISSION_DENIED
                    if (isSuccess || askType != AskType.InsistUntilSuccess) {
                        permissionResultHandlers.remove(requestCode)
                        completion(isSuccess)
                    } else {
                        // show motivation message again
                        builder.create().show()
                    }
                }

                // show motivation message
                builder.create().show()
            }
        }
    }

    private fun grantBluetoothCentralPermissions(askType: AskType, completion: (Boolean) -> Unit) {
        val wantedPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            emptyArray()
        }

        if (wantedPermissions.isEmpty() || hasPermissions(wantedPermissions)) {
            completion(true)
        } else {
            runOnUiThread {
                val requestCode = CommonConstant.BLUETOOTH_ALL_PERMISSIONS_REQUEST_CODE

                // set permission result handler
                permissionResultHandlers[requestCode] = { _ /*permissions*/, grantResults ->
                    val isSuccess = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                    if (isSuccess || askType != AskType.InsistUntilSuccess) {
                        permissionResultHandlers.remove(requestCode)
                        completion(isSuccess)
                    } else {
                        // request again
                        requestPermissionArray(wantedPermissions, requestCode)
                    }
                }

                requestPermissionArray(wantedPermissions, requestCode)
            }
        }
    }

    private fun Activity.requestPermissionArray(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }



    private val animationScan = object : Runnable {
        override fun run() {
            imgAnimation1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000)
                .withEndAction {
                    imgAnimation1.scaleX = 1f
                    imgAnimation1.scaleY = 1f
                    imgAnimation1.alpha = 1f
                }

            imgAnimation2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700)
                .withEndAction {
                    imgAnimation2.scaleX = 1f
                    imgAnimation2.scaleY = 1f
                    imgAnimation2.alpha = 1f
                }

            handlerAnimation.postDelayed(this, 1500)
        }
    }
    private fun onClickScan() {
        switchConnect.setOnClickListener {
            if (statusAnimation) {
                startScan()
            } else stopScan()
            statusAnimation = !statusAnimation
            bleRestartLifecycle()
        }
    }

    private fun stopScan() {
        animationScan.run()
        AppUtils.changeText(switchConnect, getString(R.string.stop))
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bleOnOffListener, filter)
        userWantsToScanAndConnect = true
    }

    private fun startScan() {
        handlerAnimation.removeCallbacks(animationScan)
        AppUtils.changeText(switchConnect, getString(R.string.scan))
        switchConnect.setText(R.string.scan)
        userWantsToScanAndConnect = false
        unregisterReceiver(bleOnOffListener)
    }

}