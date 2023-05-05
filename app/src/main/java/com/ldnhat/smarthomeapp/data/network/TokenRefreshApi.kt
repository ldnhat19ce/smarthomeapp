package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenRefreshApi : BaseApi {
    @FormUrlEncoded
    @POST("auth/refresh-token")
    suspend fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String?
    ): TokenResponse
}