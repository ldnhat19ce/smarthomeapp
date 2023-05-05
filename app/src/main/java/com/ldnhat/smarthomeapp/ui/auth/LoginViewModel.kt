package com.ldnhat.smarthomeapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.AuthRepository
import com.ldnhat.smarthomeapp.data.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    fun login(
        username: String,
        password: String,
        rememberMe: Boolean
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(username, password, rememberMe)
    }

    suspend fun saveAccessTokens(accessToken: String) {
        repository.saveAccessTokens(accessToken)
    }

}