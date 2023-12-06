package com.ldnhat.smarthomeapp.ui.notification_setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.repository.NotificationSettingRepository
import com.ldnhat.smarthomeapp.data.request.NotificationSettingRequest
import com.ldnhat.smarthomeapp.data.response.NotificationSettingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(private val notificationSettingRepository: NotificationSettingRepository) :
    ViewModel() {

    private val _notificationSetting: MutableLiveData<Resource<NotificationSettingResponse>> =
        MutableLiveData()
    val notificationSetting: LiveData<Resource<NotificationSettingResponse>>
        get() = _notificationSetting

    fun saveNotificationSetting(notificationSettingRequest: NotificationSettingRequest) =
        viewModelScope.launch {
            _notificationSetting.postValue(Resource.Loading)
            val notificationSettingDeferred = async {
                notificationSettingRepository.saveNotificationSetting(notificationSettingRequest)
            }
            val notificationSettingAwait = notificationSettingDeferred.await()
            if(notificationSettingAwait is Resource.Success) {
                _notificationSetting.postValue(notificationSettingAwait)
            } else if(notificationSettingAwait is Resource.Failure) {
                _notificationSetting.postValue(Resource.Failure(false, notificationSettingAwait.errorCode, notificationSettingAwait.errorBody))
            }
        }
}