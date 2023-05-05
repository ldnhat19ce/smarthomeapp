package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.NotificationSettingApi
import com.ldnhat.smarthomeapp.data.request.NotificationSettingRequest
import javax.inject.Inject

class NotificationSettingRepository @Inject constructor(private val api: NotificationSettingApi) :
    BaseRepository(api) {

    suspend fun getAllNotificationSetting(id: String) = safeApiCall {
        api.getAllNotificationSetting(id)
    }

    suspend fun saveNotificationSetting(notificationSettingRequest: NotificationSettingRequest) =
        safeApiCall {
            api.saveNotificationSetting(notificationSettingRequest)
        }

    suspend fun deleteNotificationSetting(id: String) = safeApiCall {
        api.deleteNotificationSetting(id)
    }
}