package com.ldnhat.smarthomeapp.common.connection

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}