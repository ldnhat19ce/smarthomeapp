package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.BaseApi
import com.ldnhat.smarthomeapp.data.network.SafeApiCall

abstract class BaseRepository(private val api: BaseApi) : SafeApiCall {

    suspend fun logout() = safeApiCall {
        api.logout()
    }
}