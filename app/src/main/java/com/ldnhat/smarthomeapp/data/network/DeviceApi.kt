package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.data.request.DeviceRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import retrofit2.http.*
import java.util.*

interface DeviceApi : BaseApi {

    @GET("devices")
    suspend fun getAllDevices(): List<DeviceResponse>

    @GET("devices")
    suspend fun getAllDevices(@Query("deviceType") deviceType: DeviceType): List<DeviceResponse>

    @GET("devices/{id}")
    suspend fun getDeviceById(@Path("id") id: String): DeviceResponse

    @PUT("devices/action/{id}")
    suspend fun changeActionDevice(@Path("id") id: String): DeviceResponse

    @POST("devices")
    suspend fun saveDevice(@Body deviceRequest: DeviceRequest): DeviceResponse
}