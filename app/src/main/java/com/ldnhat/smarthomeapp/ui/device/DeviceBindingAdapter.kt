package com.ldnhat.smarthomeapp.ui.device

import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse

@BindingAdapter("deviceActionBackground")
fun bindStatus(backgroundState: AppCompatTextView, action: DeviceAction?) {
    Log.d("binding status ", action.toString())
    when (action) {
        DeviceAction.ON -> {
            backgroundState.setTextColor(backgroundState.context.getColor(R.color.colorBrandBlue))
        }
        DeviceAction.OFF -> {
            backgroundState.setTextColor(backgroundState.context.getColor(R.color.colorRed))
        }
        else -> {}
    }
}

@BindingAdapter("deviceActionButtonBackground")
fun bindStatus(backgroundState: AppCompatButton, action: DeviceAction?) {
    Log.d("binding status ", action.toString())
    when (action) {
        DeviceAction.ON -> {
            backgroundState.setBackgroundColor(backgroundState.context.getColor(R.color.colorRed))
            backgroundState.text = backgroundState.context.getString(R.string.turn_off)
        }
        DeviceAction.OFF -> {
            backgroundState.setBackgroundColor(backgroundState.context.getColor(R.color.colorBrandBlue))
            backgroundState.text = backgroundState.context.getString(R.string.turn_on)
        }
        else -> {}
    }
}

@BindingAdapter("deviceTimerListData")
fun bindDeviceTimerRecyclerView(
    recyclerView: RecyclerView,
    data: Resource<List<DeviceTimerResponse>>?
) {
    val adapter = recyclerView.adapter as DeviceTimerAdapter
    when (data) {
        is Resource.Success -> {
            adapter.submitList(data.value)
        }
        else -> {}
    }
}