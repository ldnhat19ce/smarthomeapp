package com.ldnhat.smarthomeapp.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceRepository
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
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

    override fun onCleared() {
        super.onCleared()
        println("viewModel cleared")
        viewModelScope.cancel()
    }
}