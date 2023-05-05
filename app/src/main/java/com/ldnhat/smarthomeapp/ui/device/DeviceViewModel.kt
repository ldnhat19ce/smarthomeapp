package com.ldnhat.smarthomeapp.ui.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceRepository
import com.ldnhat.smarthomeapp.data.repository.DeviceTimerRepository
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceTimerRepository: DeviceTimerRepository
) : ViewModel() {

    private val _device: MutableLiveData<Resource<DeviceResponse>> = MutableLiveData()
    val device: LiveData<Resource<DeviceResponse>>
        get() = _device

    private var _deviceAction = MutableLiveData<DeviceAction>()
    val deviceAction: LiveData<DeviceAction>
        get() = _deviceAction

    private val _deviceTimers: MutableLiveData<Resource<List<DeviceTimerResponse>>> =
        MutableLiveData()
    val deviceTimers: LiveData<Resource<List<DeviceTimerResponse>>>
        get() = _deviceTimers

    private var _btnActionDevice = MutableLiveData<Boolean>()
    val btnActionDevice: LiveData<Boolean>
        get() = _btnActionDevice

    private val _actionDevice: MutableLiveData<Resource<DeviceTimerResponse>> =
        MutableLiveData()
    val actionDevice: LiveData<Resource<DeviceTimerResponse>>
        get() = _actionDevice

    fun getDeviceById(deviceId: String) = viewModelScope.launch {
        _device.postValue(Resource.Loading)
        val deviceDeferred = async { deviceRepository.getDeviceById(deviceId) }
        val deviceAwait = deviceDeferred.await()

        if (deviceAwait is Resource.Success) {
            _device.postValue(deviceAwait)
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun getAllDeviceTimer(id: String) = viewModelScope.launch {
        _deviceTimers.postValue(Resource.Loading)
        val deviceTimersDeferred = async { deviceTimerRepository.getAllDeviceTimers(id) }
        val deviceTimersAwait = deviceTimersDeferred.await()

        val deviceTimersListItem = mutableListOf<DeviceTimerResponse>()
        if (deviceTimersAwait is Resource.Success) {
            deviceTimersListItem.addAll(deviceTimersAwait.value)
            _deviceTimers.postValue(Resource.Success(deviceTimersListItem))
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun updateDeviceTimers(deviceTimers: List<DeviceTimerResponse>) {
        _deviceTimers.postValue(Resource.Success(deviceTimers))
    }

    fun onDeviceAction(deviceAction: String) {
        viewModelScope.launch {
            if (deviceAction == DeviceAction.ON.toString()) {
                _deviceAction.value = DeviceAction.ON
            } else {
                _deviceAction.value = DeviceAction.OFF
            }
        }
    }

    fun deleteDeviceTimer(id: String) = viewModelScope.launch {
        deviceTimerRepository.deleteDeviceTimer(id)
    }

    fun onChangeActionDevice(deviceId: String) {
        changeDeviceAction(deviceId)
    }

    private fun changeDeviceAction(deviceId: String) = viewModelScope.launch {
        _actionDevice.postValue(Resource.Loading)
        val actionDeviceDeferred = async { deviceRepository.changeDeviceAction(deviceId) }
        val actionDeviceAwait = actionDeviceDeferred.await()

        if (actionDeviceAwait is Resource.Success) {
            _device.postValue(actionDeviceAwait)
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun selectedStateDevice() {
        _btnActionDevice.value = true
    }

    fun selectedStateDeviceComplete() {
        _btnActionDevice.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}