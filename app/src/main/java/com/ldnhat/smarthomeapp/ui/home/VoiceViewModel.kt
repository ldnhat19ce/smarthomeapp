package com.ldnhat.smarthomeapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.SpeechDataRepository
import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceViewModel @Inject constructor(private val speechDataRepository: SpeechDataRepository) :
    ViewModel() {
    private val _speechData: MutableLiveData<Resource<SpeechDataResponse>> = MutableLiveData()
    val speechData: LiveData<Resource<SpeechDataResponse>>
        get() = _speechData

    fun handleMessageRequest(messageRequest: String) = viewModelScope.launch {
        _speechData.postValue(Resource.Loading)
        val speechDataDeferred = async { speechDataRepository.handleMessageRequest(messageRequest) }
        val speechDataAwait = speechDataDeferred.await()

        if (speechDataAwait is Resource.Success) {
            _speechData.postValue(speechDataAwait)
        } else {
            Resource.Failure(false, null, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("viewModel cleared")
        viewModelScope.cancel()
    }
}