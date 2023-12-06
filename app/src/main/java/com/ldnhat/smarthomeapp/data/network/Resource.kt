package com.ldnhat.smarthomeapp.data.network

import com.ldnhat.smarthomeapp.data.response.ErrorResponse
import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ErrorResponse?
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}