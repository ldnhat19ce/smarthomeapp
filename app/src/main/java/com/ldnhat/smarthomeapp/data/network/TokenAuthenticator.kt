package com.ldnhat.smarthomeapp.data.network

import android.content.Context
import com.ldnhat.smarthomeapp.data.UserPreferences
import com.ldnhat.smarthomeapp.data.repository.BaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    context: Context,
    private val tokenApi: TokenRefreshApi
) : Authenticator, BaseRepository(tokenApi) {
    private val appContext = context.applicationContext
    private val userPreferences = UserPreferences(appContext)

    override fun authenticate(route: Route?, response: Response): Request {
        return runBlocking {
            response.request.newBuilder()
                .header("Authorization", "Bearer ${userPreferences.accessToken.first()}")
                .build()
        }
    }
}