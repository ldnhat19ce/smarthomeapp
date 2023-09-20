package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.SpeechDataApi
import com.ldnhat.smarthomeapp.data.request.SpeechDataDTO
import com.ldnhat.smarthomeapp.data.request.SpeechDataRequest
import javax.inject.Inject

class SpeechDataRepository @Inject constructor(private val api: SpeechDataApi) :
    BaseRepository(api) {

    suspend fun getAllSpeechData() = safeApiCall {
        api.getAllSpeechData()
    }

    suspend fun handleMessageRequest(messageRequest: String) = safeApiCall {
        api.handleMessageRequest(SpeechDataDTO(messageRequest))
    }

    suspend fun updateSpeechData(speechDataDTO: SpeechDataDTO) = safeApiCall {
        api.updateSpeechData(speechDataDTO)
    }

    suspend fun deleteSpeechData(id: String) = safeApiCall {
        api.deleteSpeechData(id)
    }

    suspend fun saveSpeechData(speechDataRequest: SpeechDataRequest) = safeApiCall {
        api.saveSpeechData(speechDataRequest)
    }
}