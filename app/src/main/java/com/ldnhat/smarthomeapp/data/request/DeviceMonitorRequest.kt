package com.ldnhat.smarthomeapp.data.request

data class DeviceMonitorRequest(
    val value: String?,
    val unitMeasure: String?,
    val deviceDTO: DeviceDTO?
)
