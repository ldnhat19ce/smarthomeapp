package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.DeviceTokenRequest
import com.ldnhat.smarthomeapp.data.response.DeviceTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceTokenApi : BaseApi {

    @POST("device-token")
    suspend fun saveDeviceToken(@Body deviceTokenRequest: DeviceTokenRequest) : DeviceTokenResponse
}