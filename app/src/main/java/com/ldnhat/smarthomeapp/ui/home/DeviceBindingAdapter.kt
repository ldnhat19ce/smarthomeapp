package com.ldnhat.smarthomeapp.ui.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.data.network.Resource
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