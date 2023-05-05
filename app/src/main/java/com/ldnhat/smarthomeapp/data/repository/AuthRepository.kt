package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.data.network.AuthApi
import com.ldnhat.smarthomeapp.data.request.LoginRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val preferences: UserPreferences
) : BaseRepository(api) {

    suspend fun login(
        username: String,
        password: String,
        rememberMe: Boolean
    ) = safeApiCall {
        api.login(LoginRequest(username, password, rememberMe))
    }

    suspend fun saveAccessTokens(accessToken: String) {
        preferences.saveAccessTokens(accessToken)
    }
}