package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.request.NotificationSettingRequest
import com.ldnhat.smarthomeapp.data.response.NotificationSettingResponse
import retrofit2.http.*

interface NotificationSettingApi : BaseApi {

    @POST("notification-setting")
    suspend fun saveNotificationSetting(@Body notificationSettingRequest: NotificationSettingRequest): NotificationSettingResponse

    @GET("notification-setting/{id}")
    suspend fun getAllNotificationSetting(@Path("id") id: String): List<NotificationSettingResponse>

    @DELETE("notification-setting/{id}")
    suspend fun deleteNotificationSetting(@Path("id") id: String): Void
}