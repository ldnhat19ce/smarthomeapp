package com.ldnhat.smarthomeapp.ui.save_device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceRepository
import com.ldnhat.smarthomeapp.data.request.DeviceRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveDeviceViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
    ViewModel() {

    private val _device: MutableLiveData<Resource<DeviceResponse>> = MutableLiveData()
    val device: LiveData<Resource<DeviceResponse>>
        get() = _device

    fun saveDevice(device: DeviceRequest) = viewModelScope.launch {
        _device.postValue(Resource.Loading)
        val deviceDeferred = async { deviceRepository.saveDevice(device) }
        val deviceAwait = deviceDeferred.await()

        if(deviceAwait is Resource.Success) {
            _device.postValue(deviceAwait)
        } else if(deviceAwait is Resource.Failure) {
            _device.postValue(Resource.Failure(false, deviceAwait.errorCode, deviceAwait.errorBody))
        }
    }
}
