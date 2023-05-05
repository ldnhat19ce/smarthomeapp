package com.ldnhat.smarthomeapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.AccountRepository
import com.ldnhat.smarthomeapp.data.response.AccountResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {

    private var _account = MutableLiveData<Resource<AccountResponse>>()
    val account: LiveData<Resource<AccountResponse>>
        get() = _account

    fun getAccount() = viewModelScope.launch {
        _account.postValue(Resource.Loading)
        val accountDeferred = async { accountRepository.getAccount() }
        val accountAwait = accountDeferred.await()

        if (accountAwait is Resource.Success) {
            _account.postValue(accountAwait)
        } else {
            Resource.Failure(false, null, null)
        }
    }
}