package com.ldnhat.smarthomeapp.data.request

data class LoginRequest(
    val username: String?,
    val password: String?,
    val rememberMe: Boolean?
) {}
