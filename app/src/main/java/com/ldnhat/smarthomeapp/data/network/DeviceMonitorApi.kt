package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.DeviceMonitorRequest
import com.ldnhat.smarthomeapp.data.response.DeviceMonitorResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceMonitorApi : BaseApi {
    @GET("device-monitor/{id}")
    suspend fun getAllDeviceMonitor(@Path("id") id: String): List<DeviceMonitorResponse>

    @POST("device-monitor")
    suspend fun saveDeviceMonitor(@Body deviceMonitorRequest: DeviceMonitorRequest): DeviceMonitorResponse
}