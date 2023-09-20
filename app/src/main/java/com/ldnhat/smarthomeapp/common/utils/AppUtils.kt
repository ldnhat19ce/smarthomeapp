package com.ldnhat.smarthomeapp.common.utils

import android.os.Build
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AppUtils {
    companion object {
        const val TOKEN = "token"
        const val DEVICE_TOKEN = "device_token"
        fun convertStringToInstant(dateTime : String) : String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                    .withZone(ZoneId.systemDefault())
                val instant = Instant.parse(dateTime)
                formatter.format(instant)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }

        const val PATTERN_FORMAT = "dd-MM-yyyy HH:mm"
    }
}