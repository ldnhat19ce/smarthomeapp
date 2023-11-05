package com.ldnhat.smarthomeapp.data.response

data class DeviceMonitorResponse(
    val id: String,
    val value: String,
    val unitMeasure: String,
    val deviceDTO: DeviceResponse,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String,
    val minValue : String,
    val maxValue : String
) {
}