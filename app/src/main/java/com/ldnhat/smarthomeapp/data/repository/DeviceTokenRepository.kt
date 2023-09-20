package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.DeviceTokenApi
import com.ldnhat.smarthomeapp.data.request.DeviceTokenRequest
import javax.inject.Inject

class DeviceTokenRepository @Inject constructor(private val api: DeviceTokenApi) :
    BaseRepository(api) {

    suspend fun saveDeviceToken(deviceTokenRequest: DeviceTokenRequest) = safeApiCall {
        api.saveDeviceToken(deviceTokenRequest)
    }
}