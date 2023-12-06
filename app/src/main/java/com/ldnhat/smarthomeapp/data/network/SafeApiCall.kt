package com.ldnhat.smarthomeapp.data.network

import com.google.gson.Gson
import com.ldnhat.smarthomeapp.data.response.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        val gson = Gson()
                        val message: ErrorResponse = gson.fromJson(throwable.response()?.errorBody()?.charStream(), ErrorResponse::class.java)
                        Resource.Failure(false, throwable.code(), message)
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}