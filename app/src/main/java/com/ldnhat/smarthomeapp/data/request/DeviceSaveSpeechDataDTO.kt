package com.ldnhat.smarthomeapp.data.request

import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType

data class DeviceSaveSpeechDataDTO(
    val id : String,
    val name: String?,
    val deviceType: DeviceType?,
    val deviceAction: DeviceAction?
) {
}