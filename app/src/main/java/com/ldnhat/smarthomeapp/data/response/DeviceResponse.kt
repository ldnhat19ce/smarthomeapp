package com.ldnhat.smarthomeapp.data.response

import android.os.Parcelable
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.data.NoArgAnnotation
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceResponse(
    var id: String = "",
    var name: String = "",
    var deviceType: DeviceType = DeviceType.MONITOR,
    var deviceAction: DeviceAction = DeviceAction.NOTHING,
    var createdBy: String = "",
    var createdDate: String = "",
    var lastModifiedBy: String = "",
    var lastModifiedDate: String = ""
) : Parcelable {
    val createDateConverter: String
        get() = AppUtils.convertStringToInstant(createdDate)

    val lastModifiedDateConverter: String
        get() = AppUtils.convertStringToInstant(lastModifiedDate)
}
