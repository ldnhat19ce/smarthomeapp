package com.ldnhat.smarthomeapp.ui.save_speech_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.DeviceRepository
import com.ldnhat.smarthomeapp.data.repository.SpeechDataRepository
import com.ldnhat.smarthomeapp.data.request.SpeechDataRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveSpeechDataViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val speechDataRepository: SpeechDataRepository
) : ViewModel() {

    private val _speechData: MutableLiveData<Resource<SpeechDataResponse>> = MutableLiveData()
    val speechData: LiveData<Resource<SpeechDataResponse>>
        get() = _speechData

    private val _devices: MutableLiveData<Resource<List<DeviceResponse>>> = MutableLiveData()
    val devices: LiveData<Resource<List<DeviceResponse>>>
        get() = _devices

    init {
        getAllDevices()
    }

    private fun getAllDevices() = viewModelScope.launch {
        _devices.postValue(Resource.Loading)
        val devicesDeferred = async { deviceRepository.getAllDevices(DeviceType.CONTROL) }
        val deviceAwait = devicesDeferred.await()

        val deviceListItem = mutableListOf<DeviceResponse>()
        if (deviceAwait is Resource.Success) {
            deviceListItem.addAll(deviceAwait.value)
            _devices.postValue(Resource.Success(deviceListItem))
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun saveSpeechData(speechData: SpeechDataRequest) = viewModelScope.launch {
        _speechData.postValue(Resource.Loading)
        val speechDataDeferred = async { speechDataRepository.saveSpeechData(speechData) }
        val speechDataAwait = speechDataDeferred.await()
        _speechData.postValue(speechDataAwait)
    }
}