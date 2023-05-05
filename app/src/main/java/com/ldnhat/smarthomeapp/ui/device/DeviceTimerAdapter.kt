package com.ldnhat.smarthomeapp.ui.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.data.response.DeviceTimerResponse
import com.ldnhat.smarthomeapp.databinding.ItemDeviceTimerBinding

class DeviceTimerAdapter(private val clickListener: OnClickListener) :
    ListAdapter<DeviceTimerResponse, DeviceTimerAdapter.DeviceTimerViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceTimerViewHolder {
        return DeviceTimerViewHolder(ItemDeviceTimerBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DeviceTimerViewHolder, position: Int) {
        val deviceTimer = getItem(position)
        holder.bind(deviceTimer, clickListener, position)
    }

    class DeviceTimerViewHolder(private var binding: ItemDeviceTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(deviceTimer: DeviceTimerResponse, clickListener: OnClickListener, position: Int) {
            binding.deviceTimer = deviceTimer
            binding.clickListener = clickListener
            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DeviceTimerResponse>() {
        override fun areItemsTheSame(
            oldItem: DeviceTimerResponse,
            newItem: DeviceTimerResponse
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: DeviceTimerResponse,
            newItem: DeviceTimerResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (deviceTimer: DeviceTimerResponse, position: Int) -> Unit) {
        fun onclick(deviceTimer: DeviceTimerResponse, position: Int) =
            clickListener(deviceTimer, position)
    }
}