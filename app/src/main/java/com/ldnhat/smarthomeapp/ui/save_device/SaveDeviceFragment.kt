package com.ldnhat.smarthomeapp.ui.save_device

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
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.request.DeviceRequest
import com.ldnhat.smarthomeapp.databinding.FragmentCreateDeviceBinding
import com.ldnhat.smarthomeapp.ui.enable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveDeviceFragment : Fragment() {
    private val viewModel by viewModels<SaveDeviceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentCreateDeviceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_device, container, false)
        addSpinner(binding)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(SaveDeviceFragmentDirections.actionSaveDeviceToHome())
        }

        binding.btnCreateDevice.enable(false)

        binding.edDeviceName.addTextChangedListener {
            binding.btnCreateDevice.enable(it.toString().isNotEmpty())
        }

        binding.btnCreateDevice.setOnClickListener {
            val name = binding.edDeviceName.text.toString().trim()
            val deviceType = DeviceType.valueOf(binding.spDeviceType.selectedItem.toString())
            val deviceAction = DeviceAction.valueOf(binding.spDeviceAction.selectedItem.toString())

            viewModel.saveDevice(DeviceRequest(name, deviceType, deviceAction))
        }

        viewModel.device.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    findNavController().navigate(SaveDeviceFragmentDirections.actionSaveDeviceToHome())
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

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun addSpinner(binding: FragmentCreateDeviceBinding) {
        val deviceTypeAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.deviceTypeList)
        )
        val deviceActionAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.deviceActionList)
        )

        binding.spDeviceType.adapter = deviceTypeAdapter
        binding.spDeviceAction.adapter = deviceActionAdapter
    }
}