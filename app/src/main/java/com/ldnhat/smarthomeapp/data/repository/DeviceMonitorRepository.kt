package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.DeviceMonitorApi
import com.ldnhat.smarthomeapp.data.request.DeviceMonitorRequest
import javax.inject.Inject

class DeviceMonitorRepository @Inject constructor(
    private val api: DeviceMonitorApi
) : BaseRepository(api) {

    suspend fun saveDeviceMonitor(deviceMonitorRequest: DeviceMonitorRequest) = safeApiCall {
        api.saveDeviceMonitor(deviceMonitorRequest)
    }

    suspend fun getAllDeviceMonitor(id: String) = safeApiCall {
        api.getAllDeviceMonitor(id)
    }

    suspend fun getRangeDeviceMonitor(id : String) = safeApiCall {
        api.getRangeDeviceMonitor(id)
    }

    suspend fun getListRangeDeviceMonitor(id: String, type : String) = safeApiCall {
        api.getListRangeDeviceMonitor(id, type)
    }
}