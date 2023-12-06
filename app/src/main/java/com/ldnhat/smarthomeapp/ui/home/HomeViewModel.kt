package com.ldnhat.smarthomeapp.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceMonitorRepository
import com.ldnhat.smarthomeapp.data.repository.DeviceRepository
import com.ldnhat.smarthomeapp.data.response.DeviceMonitorResponse
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject
    constructor(private val deviceRepository: DeviceRepository,
                private val deviceMonitorRepository: DeviceMonitorRepository) :
    ViewModel() {

    private val _devices: MutableLiveData<Resource<List<DeviceResponse>>> = MutableLiveData()
    val devices: LiveData<Resource<List<DeviceResponse>>>
        get() = _devices

    private var _navigatedToSelectedDevice = MutableLiveData<DeviceResponse>()

    val navigatedToSelectedDevice: LiveData<DeviceResponse>
        get() = _navigatedToSelectedDevice

    private val _deviceInit: MutableLiveData<DeviceResponse> = MutableLiveData()
    val deviceInit: LiveData<DeviceResponse>
        get() = _deviceInit

    private val _deviceMonitorRange: MutableLiveData<Resource<DeviceMonitorResponse>> = MutableLiveData()
    val deviceMonitorRange: LiveData<Resource<DeviceMonitorResponse>>
        get() = _deviceMonitorRange

    private val _currentDeviceMonitorValue : MutableLiveData<String> = MutableLiveData()
    val currentDeviceMonitorValue : LiveData<String>
        get() = _currentDeviceMonitorValue

    private val _lastModifiedDate : MutableLiveData<String> = MutableLiveData()
    val lastModifiedDate : LiveData<String>
        get() = _lastModifiedDate

    init {
        getAllDevices()
    }

    private fun getAllDevices() = viewModelScope.launch {
        _devices.postValue(Resource.Loading)
        val devicesDeferred = async { deviceRepository.getAllDevices() }
        val deviceAwait = devicesDeferred.await()

        val deviceListItem = mutableListOf<DeviceResponse>()
        if (deviceAwait is Resource.Success) {
            deviceListItem.addAll(deviceAwait.value)
            _devices.postValue(Resource.Success(deviceListItem))
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun getDeviceMonitorRange(device : DeviceResponse) = viewModelScope.launch {
        _deviceMonitorRange.postValue(Resource.Loading)
        val deviceMonitorRangeDeferred = async { deviceMonitorRepository.getRangeDeviceMonitor(device.id) }
        var deviceMonitorRangeAwait = deviceMonitorRangeDeferred.await()

        if (deviceMonitorRangeAwait is Resource.Success) {
            deviceMonitorRangeAwait.value.unitMeasure = device.unitMeasure
            _deviceMonitorRange.postValue(deviceMonitorRangeAwait)
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun setCurrentDeviceMonitorValue(value : String) = viewModelScope.launch {
        _currentDeviceMonitorValue.postValue(value)
    }

    fun displayDetailDetail(device: DeviceResponse) {
        _navigatedToSelectedDevice.value = device
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun displayDeviceDetailComplete() {
        _navigatedToSelectedDevice.value = null
    }

    fun setDeviceInit(deviceResponse: DeviceResponse) {
        _deviceInit.value = deviceResponse
    }

    fun setLastModifiedDate(value: String) {
        _lastModifiedDate.postValue(value)
    }

    override fun onCleared() {
        super.onCleared()
        println("viewModel cleared")
        viewModelScope.cancel()
    }
}