package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface DeviceTimerApi : BaseApi {

    @GET("device-timer/{id}")
    suspend fun getAllDeviceTimers(@Path("id") id: String): List<DeviceTimerResponse>

    @DELETE("device-timer/{id}")
    suspend fun deleteDeviceTimer(@Path("id") id: String) : Void
}