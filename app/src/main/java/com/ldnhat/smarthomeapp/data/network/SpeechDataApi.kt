package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.SpeechDataDTO
import com.ldnhat.smarthomeapp.data.request.SpeechDataRequest
import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import retrofit2.http.*

interface SpeechDataApi : BaseApi {
    @GET("speech-data")
    suspend fun getAllSpeechData(): List<SpeechDataResponse>

    @POST("speech-data/text")
    suspend fun handleMessageRequest(@Body speechDataDTO: SpeechDataDTO): SpeechDataResponse

    @DELETE("speech-data/{id}")
    suspend fun deleteSpeechData(@Path("id") id: String): Void

    @PUT("speech-data")
    suspend fun updateSpeechData(@Body speechDataDTO: SpeechDataDTO): SpeechDataResponse

    @POST("speech-data")
    suspend fun saveSpeechData(@Body speechDataRequest: SpeechDataRequest): SpeechDataResponse
}