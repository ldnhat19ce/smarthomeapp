package com.ldnhat.smarthomeapp.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AppUtils {
    companion object {
        const val TOKEN = "token"
        const val DEVICE_TOKEN = "device_token"
        fun convertStringToInstant(dateTime: String): String {
            Log.d("datetime", dateTime)
            val localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            return localDateTime.format(DateTimeFormatter.ofPattern(PATTERN_FORMAT))
        }

        fun convertStringToLocalDateTime(dateTime: String): String {
            val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(CURRENT_ZONE)
            val localDateTime = LocalDateTime.parse(dateTime)
            return formatter.format(localDateTime)
        }

        private val CURRENT_ZONE: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

        private const val PATTERN_FORMAT = "dd-MM-yyyy HH:mm"

        @SuppressLint("SetTextI18n")
        fun appendLog(
            message: String,
            activity: Activity,
            textView: TextView,
            scrollView: ScrollView
        ) {
            activity.runOnUiThread {
                val strTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                textView.text = textView.text.toString() + "\n$strTime $message"

                // scroll after delay, because textView has to be updated first
                Handler(Looper.getMainLooper()).postDelayed({
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }, 16)
            }
        }

        fun changeText(button: Button, text: String) {
            button.setText(text)
        }

        fun formatDeviceValue(value : String) : String {
            val valueSplit = value.split(".")

            return if(valueSplit.size == 2) {
                if(valueSplit[1].toBigDecimal().compareTo(BigDecimal(0)) == 0) {
                    valueSplit[0]
                } else {
                    value
                }
            } else {
                valueSplit[0]
            }
        }

        fun additionalZero(value : Int) : String {
            if(value < 10) {
                return "0$value"
            }
            return value.toString()
        }

        fun getZone(): ZoneId? {
            return ZoneId.of("Asia/Ho_Chi_Minh")
        }
    }
}