package com.ldnhat.smarthomeapp.data.response

data class NotificationSettingResponse(
    val id: String,
    val title: String,
    val message: String,
    val value: String,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
)
