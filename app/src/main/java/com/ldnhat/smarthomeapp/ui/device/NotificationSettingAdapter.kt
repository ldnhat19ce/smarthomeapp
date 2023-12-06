package com.ldnhat.smarthomeapp.ui.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.data.response.NotificationSettingResponse
import com.ldnhat.smarthomeapp.databinding.ItemNotificationSettingBinding

class NotificationSettingAdapter(private val clickListener: OnClickListener) :
    ListAdapter<NotificationSettingResponse, NotificationSettingAdapter.NotificationSettingViewHolder>(
        DiffCallback
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationSettingViewHolder {
        return NotificationSettingViewHolder(ItemNotificationSettingBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: NotificationSettingViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, position)
    }

    class NotificationSettingViewHolder(private var binding: ItemNotificationSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            notificationSetting: NotificationSettingResponse,
            clickListener: OnClickListener,
            position: Int
        ) {
            binding.notificationSetting = notificationSetting
            binding.clickListener = clickListener
            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NotificationSettingResponse>() {
        override fun areItemsTheSame(
            oldItem: NotificationSettingResponse,
            newItem: NotificationSettingResponse
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationSettingResponse,
            newItem: NotificationSettingResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (notificationSetting: NotificationSettingResponse, position: Int) -> Unit) {
        fun onclick(notificationSetting: NotificationSettingResponse, position: Int) =
            clickListener(notificationSetting, position)
    }
}