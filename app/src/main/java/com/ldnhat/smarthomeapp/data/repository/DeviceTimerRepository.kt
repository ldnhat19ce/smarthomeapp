package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.DeviceTimerApi
import javax.inject.Inject

class DeviceTimerRepository @Inject constructor(private val api: DeviceTimerApi) :
    BaseRepository(api) {

    suspend fun getAllDeviceTimers(id: String) = safeApiCall {
        api.getAllDeviceTimers(id)
    }

    suspend fun deleteDeviceTimer(id: String) = safeApiCall {
        api.deleteDeviceTimer(id)
    }
}