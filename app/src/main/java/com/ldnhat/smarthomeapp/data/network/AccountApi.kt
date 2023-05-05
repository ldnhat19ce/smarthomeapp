package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.AccountResponse
import retrofit2.http.GET

interface AccountApi : BaseApi {

    @GET("account")
    suspend fun getAccount() : AccountResponse
}