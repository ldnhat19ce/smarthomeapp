package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.LoginResponse
import com.ldnhat.smarthomeapp.data.request.LoginRequest
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi : BaseApi {
    @POST("authenticate")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}