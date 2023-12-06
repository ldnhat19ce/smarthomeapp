package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.DeviceTimerRequest
import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse
import retrofit2.http.*

interface DeviceTimerApi : BaseApi {

    @GET("device-timer/{id}")
    suspend fun getAllDeviceTimers(@Path("id") id: String): List<DeviceTimerResponse>

    @DELETE("device-timer/{id}")
    suspend fun deleteDeviceTimer(@Path("id") id: String): Void

    @POST("device-timer")
    suspend fun saveDeviceTimer(@Body deviceTimerRequest: DeviceTimerRequest): DeviceTimerResponse
}