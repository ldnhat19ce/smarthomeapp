package com.ldnhat.smarthomeapp.data.request

data class NotificationSettingRequest(
    val title: String,
    val message: String,
    val value: String,
    val deviceDTO: DeviceDTO?
) {}