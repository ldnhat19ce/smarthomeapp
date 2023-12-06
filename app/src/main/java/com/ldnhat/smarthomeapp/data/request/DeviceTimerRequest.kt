package com.ldnhat.smarthomeapp.data.request

import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction

data class DeviceTimerRequest(
    val time: String?,
    val deviceAction: DeviceAction?,
    val deviceDTO: DeviceDTO?
)
