package com.ldnhat.smarthomeapp.data.request

data class SpeechDataRequest(
    val messageRequest: String,
    val messageResponse: String,
    val deviceDTO: DeviceSaveSpeechDataDTO
) {

}
