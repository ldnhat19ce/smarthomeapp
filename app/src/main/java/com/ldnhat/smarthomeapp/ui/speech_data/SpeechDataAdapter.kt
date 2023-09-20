package com.ldnhat.smarthomeapp.ui.speech_data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldnhat.smarthomeapp.data.response.SpeechDataResponse
import com.ldnhat.smarthomeapp.databinding.ItemSpeechDataBinding

class SpeechDataAdapter(private val clickListener: OnClickListener) :
    ListAdapter<SpeechDataResponse, SpeechDataAdapter.SpeechDataViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeechDataViewHolder {
        return SpeechDataViewHolder(ItemSpeechDataBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: SpeechDataViewHolder, position: Int) {
        val speechData = getItem(position)
        holder.bind(speechData, clickListener, position)
    }

    class SpeechDataViewHolder(private var binding: ItemSpeechDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(speechData: SpeechDataResponse, clickListener: OnClickListener, position: Int) {
            binding.speechData = speechData
            binding.clickListener = clickListener
            binding.position = position
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SpeechDataResponse>() {
        override fun areItemsTheSame(
            oldItem: SpeechDataResponse,
            newItem: SpeechDataResponse
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: SpeechDataResponse,
            newItem: SpeechDataResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (speechData: SpeechDataResponse, position: Int) -> Unit) {
        fun onclick(speechData: SpeechDataResponse, position: Int) =
            clickListener(speechData, position)
    }
}