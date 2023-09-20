package com.ldnhat.smarthomeapp.ui.speech_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.SpeechDataRepository
import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpeechDataViewModel @Inject constructor(private val speechDataRepository: SpeechDataRepository) :
    ViewModel() {

    private val _speechData: MutableLiveData<Resource<List<SpeechDataResponse>>> = MutableLiveData()
    val speechData: LiveData<Resource<List<SpeechDataResponse>>>
        get() = _speechData

    fun getAllSpeechData() = viewModelScope.launch {
        _speechData.postValue(Resource.Loading)
        val speechDataDeferred = async { speechDataRepository.getAllSpeechData() }
        val speechDataAwait = speechDataDeferred.await()

        val speechDataListItem = mutableListOf<SpeechDataResponse>()
        if (speechDataAwait is Resource.Success) {
            speechDataListItem.addAll(speechDataAwait.value)
            _speechData.postValue(Resource.Success(speechDataListItem))
        } else {
            Resource.Failure(false, null, null)
        }
    }

    fun deleteSpeechData(id: String) = viewModelScope.launch {
        speechDataRepository.deleteSpeechData(id)
    }

    fun updateSpeechData(speechDataResponse: List<SpeechDataResponse>) {
        _speechData.postValue(Resource.Success(speechDataResponse))
    }
}