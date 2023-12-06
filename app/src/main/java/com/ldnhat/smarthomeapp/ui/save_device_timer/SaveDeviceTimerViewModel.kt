package com.ldnhat.smarthomeapp.ui.save_device_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceTimerRepository
import com.ldnhat.smarthomeapp.data.request.DeviceTimerRequest
import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveDeviceTimerViewModel @Inject constructor(private val deviceTimerRepository: DeviceTimerRepository) :
    ViewModel() {
    private val _deviceTimer: MutableLiveData<Resource<DeviceTimerResponse>> = MutableLiveData()
    val deviceTimer: LiveData<Resource<DeviceTimerResponse>>
        get() = _deviceTimer

    fun saveDeviceTimer(deviceTimer: DeviceTimerRequest) = viewModelScope.launch {
        _deviceTimer.postValue(Resource.Loading)
        val deviceTimerDeferred = async { deviceTimerRepository.saveDeviceTimer(deviceTimer) }
        val deviceTimerAwait = deviceTimerDeferred.await()

        if(deviceTimerAwait is Resource.Success) {
            _deviceTimer.postValue(deviceTimerAwait)
        } else if(deviceTimerAwait is Resource.Failure) {
            _deviceTimer.postValue(Resource.Failure(false, deviceTimerAwait.errorCode, deviceTimerAwait.errorBody))
        }
    }
}