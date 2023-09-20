package com.ldnhat.smarthomeapp.ui.save_speech_data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.base.ErrorApi
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.request.DeviceSaveSpeechDataDTO
import com.ldnhat.smarthomeapp.data.request.SpeechDataRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.FragmentSaveSpeechDataBinding
import com.ldnhat.smarthomeapp.ui.enable
import dagger.hilt.android.AndroidEntryPoint
import kotlin.streams.toList

@AndroidEntryPoint
class FragmentSaveSpeechData : Fragment() {
    private val viewModel by viewModels<SaveSpeechDataViewModel>()
    private val deviceList = mutableListOf<DeviceResponse>()
    private lateinit var deviceDTO: DeviceSaveSpeechDataDTO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSaveSpeechDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_speech_data, container, false)
        viewModel.devices.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val deviceNameList =
                        it.value.stream().map { deviceResponse -> deviceResponse.name }.toList()
                    addSpinner(binding, deviceNameList)
                    deviceList.addAll(it.value)
                }
                else -> {}
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(FragmentSaveSpeechDataDirections.actionSaveToSpeechData())
        }

        binding.btnSaveSpeechData.enable(false)

        binding.edMessageResponse.addTextChangedListener {
            val messageRequest = binding.edMessageRequest.text.toString().trim()
            binding.btnSaveSpeechData.enable(
                messageRequest.isNotEmpty() && it.toString().isNotEmpty()
            )
        }

        binding.btnSaveSpeechData.setOnClickListener {
            val messageRequest = binding.edMessageRequest.text.toString().trim()
            val messageResponse = binding.edMessageResponse.text.toString().trim()
            val deviceAction = DeviceAction.valueOf(binding.spDeviceAction.selectedItem.toString())
            val deviceName = binding.spDevice.selectedItem.toString()

            val device = deviceList.stream().filter { it.name == deviceName }.findFirst()
            device.ifPresent {
                deviceDTO = DeviceSaveSpeechDataDTO(it.id, it.name, it.deviceType, deviceAction)
            }
            viewModel.saveSpeechData(SpeechDataRequest(messageRequest, messageResponse, deviceDTO))

            viewModel.speechData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        findNavController().navigate(FragmentSaveSpeechDataDirections.actionSaveToSpeechData())
                    }
                    is Resource.Failure -> {
                        binding.txtError.visibility = View.VISIBLE
                        val error: ErrorApi =
                            Gson().fromJson(it.errorBody?.string().toString(), ErrorApi::class.java)
                        binding.txtError.text = error.title
                        Log.d("error_api", it.toString())

                    }
                }
            }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun addSpinner(binding: FragmentSaveSpeechDataBinding, device: List<String>) {
        val deviceActionAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.deviceActionList)
        )

        val deviceAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, device
        )

        binding.spDevice.adapter = deviceAdapter
        binding.spDeviceAction.adapter = deviceActionAdapter
    }
}