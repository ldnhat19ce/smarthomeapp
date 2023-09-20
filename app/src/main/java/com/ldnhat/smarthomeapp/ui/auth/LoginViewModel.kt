package com.ldnhat.smarthomeapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.AuthRepository
import com.ldnhat.smarthomeapp.data.repository.DeviceTokenRepository
import com.ldnhat.smarthomeapp.data.request.DeviceTokenRequest
import com.ldnhat.smarthomeapp.data.response.DeviceTokenResponse
import com.ldnhat.smarthomeapp.data.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val deviceTokenRepository: DeviceTokenRepository
) : ViewModel() {
    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _deviceToken: MutableLiveData<Resource<DeviceTokenResponse>> = MutableLiveData()

    val deviceToken: LiveData<Resource<DeviceTokenResponse>>
        get() = _deviceToken

    fun login(
        username: String,
        password: String,
        rememberMe: Boolean
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(username, password, rememberMe)
    }

    fun saveDeviceToken(deviceTokenRequest: DeviceTokenRequest) = viewModelScope.launch {
        _deviceToken.postValue(Resource.Loading)
        val deviceTokenDeferred =
            async { deviceTokenRepository.saveDeviceToken(deviceTokenRequest) }
        val deviceTokenAwait = deviceTokenDeferred.await()
        _deviceToken.postValue(deviceTokenAwait)
    }

    suspend fun saveAccessTokens(accessToken: String) {
        repository.saveAccessTokens(accessToken)
    }

}