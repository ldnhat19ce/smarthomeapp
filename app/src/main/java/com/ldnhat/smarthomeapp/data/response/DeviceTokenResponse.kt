package com.ldnhat.smarthomeapp.data.response

data class DeviceTokenResponse(
    val id: String,
    val token: String,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
)
