package com.ldnhat.smarthomeapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.ItemDevicesBinding

class DeviceGridAdapter(private val clickListener: OnClickListener) :
    ListAdapter<DeviceResponse, DeviceGridAdapter.DeviceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(ItemDevicesBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onclick(device)
        }
        holder.bind(device)
    }

    class DeviceViewHolder(private var binding: ItemDevicesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: DeviceResponse) {
            binding.device = device
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DeviceResponse>() {
        override fun areItemsTheSame(oldItem: DeviceResponse, newItem: DeviceResponse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DeviceResponse, newItem: DeviceResponse): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (device: DeviceResponse) -> Unit) {
        fun onclick(device: DeviceResponse) = clickListener(device)
    }
}