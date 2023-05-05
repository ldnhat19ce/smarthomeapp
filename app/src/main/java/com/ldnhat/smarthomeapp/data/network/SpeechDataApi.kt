package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import com.ldnhat.smarthomeapp.data.request.SpeechDataDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SpeechDataApi : BaseApi {
    @GET("speech-data")
    suspend fun getAllSpeechData(): List<SpeechDataResponse>

    @POST("speech-data/text")
    suspend fun handleMessageRequest(@Body speechDataDTO : SpeechDataDTO) : SpeechDataResponse

    @DELETE("speech-data/{id}")
    suspend fun deleteSpeechData(@Path("id") id : String) : Void

    @PUT("speech-data")
    suspend fun updateSpeechData(@Body speechDataDTO: SpeechDataDTO) : SpeechDataResponse
}