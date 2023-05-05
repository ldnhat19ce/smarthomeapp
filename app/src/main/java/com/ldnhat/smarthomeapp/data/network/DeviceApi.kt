package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.DeviceRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeviceApi : BaseApi {

    @GET("devices")
    suspend fun getAllDevices() : List<DeviceResponse>

    @GET("devices/{id}")
    suspend fun getDeviceById(@Path("id") id : String) : DeviceResponse

    @PUT("devices/action/{id}")
    suspend fun changeActionDevice(@Path("id") id : String) : DeviceResponse

    @POST("devices")
    suspend fun saveDevice(@Body deviceRequest : DeviceRequest) : DeviceResponse
}