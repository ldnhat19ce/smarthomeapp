package com.ldnhat.smarthomeapp.data.response

import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import java.time.Instant

class DeviceTimerResponse(
    val id: String,
    val time: String,
    val deviceAction: DeviceAction,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
) {
    val timeStr : String
    get() = AppUtils.convertStringToInstant(time)
}