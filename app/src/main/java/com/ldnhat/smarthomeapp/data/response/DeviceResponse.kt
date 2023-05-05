package com.ldnhat.smarthomeapp.data.response

import android.os.Parcelable
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceResponse(
    val id: String,
    val name: String,
    val deviceType: DeviceType,
    val deviceAction: DeviceAction,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
) : Parcelable {
    val createDateConverter: String
        get() = AppUtils.convertStringToInstant(createdDate)

    val lastModifiedDateConverter: String
        get() = AppUtils.convertStringToInstant(lastModifiedDate)
}
