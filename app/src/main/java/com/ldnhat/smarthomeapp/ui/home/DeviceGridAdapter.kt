package com.ldnhat.smarthomeapp.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.ItemDevices2Binding

class DeviceGridAdapter(private val clickListener: OnClickListener) :
    ListAdapter<DeviceResponse, DeviceGridAdapter.DeviceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(ItemDevices2Binding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onclick(device)
        }
        holder.bind(device)
    }

    class DeviceViewHolder(private var binding: ItemDevices2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(device: DeviceResponse) {
            binding.device = device
            if(device.deviceType == DeviceType.MONITOR) {
                val db = Firebase.firestore

                db.collection("develop").document("device_monitor")
                    .collection(device.createdBy)
                    .document(device.id)
                    .addSnapshotListener{snapshot, e ->
                        if (e != null) {
                            Log.d("Firebase", "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            binding.sunrise.text = "${AppUtils.formatDeviceValue(snapshot.data?.get("value").toString())}${device.unitMeasure}"
                        }
                    }
            } else {
                binding.sunrise.text = device.deviceAction.toString()
                if (device.deviceAction == DeviceAction.OFF) {
                    binding.sunrise.setTextColor(Color.RED)
                } else if (device.deviceAction == DeviceAction.ON) {
                    binding.sunrise.setTextColor(Color.BLUE)
                }
            }
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