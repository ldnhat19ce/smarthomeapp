package com.ldnhat.smarthomeapp.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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

        @SuppressLint("SetTextI18n")
        fun appendLog(message: String, activity: Activity, textView: TextView, scrollView: ScrollView) {
            activity.runOnUiThread {
                val strTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                textView.text = textView.text.toString() + "\n$strTime $message"

                // scroll after delay, because textView has to be updated first
                Handler(Looper.getMainLooper()).postDelayed({
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }, 16)
            }
        }

        fun changeText(button : Button, text : String) {
            button.setText(text)
        }
    }
}