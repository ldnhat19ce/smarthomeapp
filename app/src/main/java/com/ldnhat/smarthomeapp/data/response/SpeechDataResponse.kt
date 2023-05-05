package com.ldnhat.smarthomeapp.data.response

data class SpeechDataResponse(
    val id: String,
    val messageRequest: String,
    val messageResponse: String,
    val deviceDTO: DeviceResponse,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
) {
}