package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.DeviceApi
import com.ldnhat.smarthomeapp.data.request.DeviceRequest
import javax.inject.Inject

class DeviceRepository @Inject constructor(private val api: DeviceApi) : BaseRepository(api) {

    suspend fun getAllDevices() = safeApiCall {
        api.getAllDevices()
    }

    suspend fun getDeviceById(id: String) = safeApiCall {
        api.getDeviceById(id)
    }

    suspend fun changeDeviceAction(id: String) = safeApiCall {
        api.changeActionDevice(id)
    }

    suspend fun saveDevice(deviceRequest: DeviceRequest) = safeApiCall {
        api.saveDevice(deviceRequest)
    }
}