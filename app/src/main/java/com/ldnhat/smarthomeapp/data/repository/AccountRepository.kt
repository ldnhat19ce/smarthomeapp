package com.ldnhat.smarthomeapp.data.repository

import com.ldnhat.smarthomeapp.data.network.AccountApi
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val api: AccountApi
) : BaseRepository(api) {

    suspend fun getAccount() = safeApiCall {
        api.getAccount()
    }
}