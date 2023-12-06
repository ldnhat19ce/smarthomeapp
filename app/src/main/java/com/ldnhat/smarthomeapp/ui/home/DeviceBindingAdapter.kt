package com.ldnhat.smarthomeapp.ui.home

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.DeviceMonitorResponse
import com.ldnhat.smarthomeapp.data.response.DeviceResponse

@BindingAdapter("deviceListData")
fun bindDeviceRecyclerView(recyclerView: RecyclerView, data: Resource<List<DeviceResponse>>?) {
    val adapter = recyclerView.adapter as DeviceGridAdapter
    when (data) {
        is Resource.Success -> {
            adapter.submitList(data.value)
        }
        else -> {}
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("minData")
fun bindMinData(textView: TextView, data: Resource<DeviceMonitorResponse>?) {
    when (data) {
        is Resource.Success -> {
            textView.text = "Min value: " + AppUtils.formatDeviceValue(data.value.minValue) + data.value.unitMeasure
        }
        else -> {}
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("maxData")
fun bindMaxData(textView: TextView, data: Resource<DeviceMonitorResponse>?) {
    when (data) {
        is Resource.Success -> {
            textView.text = "Max value: " + AppUtils.formatDeviceValue(data.value.maxValue) + data.value.unitMeasure
        }
        else -> {}
    }
}