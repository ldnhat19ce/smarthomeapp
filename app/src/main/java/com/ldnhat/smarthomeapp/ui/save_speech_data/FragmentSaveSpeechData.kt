package com.ldnhat.smarthomeapp.ui.save_speech_data

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

    @SuppressLint("SetTextI18n")
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
                    listenerDropdown(binding)
                    deviceList.addAll(it.value)
                }
                else -> {}
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
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
                        findNavController().popBackStack()
                    }
                    is Resource.Failure -> {
                        binding.txtError.visibility = View.VISIBLE
                        if(it.errorBody != null) {
                            binding.txtError.text = it.errorBody.title
                        }
                    }
                }
            }
        }

        customHeaderBar(binding)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun addSpinner(binding: FragmentSaveSpeechDataBinding, device: List<String>) {
        val deviceActionAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.deviceActionList).filter { v -> !v.equals("NOTHING") }
        )

        val deviceActionPosition: Int =
            deviceActionAdapter.getPosition(resources.getStringArray(R.array.deviceActionList).filter { v -> !v.equals("NOTHING") }[0])

        val deviceAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, device
        )

        val devicePosition: Int = deviceAdapter.getPosition(device[0])

        binding.spDevice.adapter = deviceAdapter
        binding.spDevice.setSelection(devicePosition)

        binding.spDeviceAction.adapter = deviceActionAdapter
        binding.spDeviceAction.setSelection(deviceActionPosition)
    }

    private fun listenerDropdown(binding: FragmentSaveSpeechDataBinding) {
        binding.spDeviceAction.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected: TextView = parent?.getChildAt(0) as TextView
                selected.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spDevice.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected: TextView = parent?.getChildAt(0) as TextView
                selected.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun customHeaderBar(binding: FragmentSaveSpeechDataBinding) {
        binding.vhSaveSpeechData.imvBack.setOnClickListener {
            if (findNavController().popBackStack().not()) {
                requireActivity().finish()
            }
        }
    }
}