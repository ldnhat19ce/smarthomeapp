package com.ldnhat.smarthomeapp.ui.save_device

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ldnhat.smarthomeapp.R
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
            findNavController().popBackStack()
        }

        binding.btnCreateDevice.enable(false)

        binding.edDeviceName.addTextChangedListener {
            binding.btnCreateDevice.enable(it.toString().isNotEmpty())
        }

        binding.spDeviceType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (resources.getStringArray(R.array.deviceTypeList)[position].equals("MONITOR")) {
                    binding.txtUnitMeasure.visibility = View.VISIBLE
                    binding.edUnitMeasure.visibility = View.VISIBLE
                    binding.txtDeviceAction.visibility = View.GONE
                    binding.spDeviceAction.visibility = View.GONE
                } else {
                    binding.txtUnitMeasure.visibility = View.GONE
                    binding.edUnitMeasure.visibility = View.GONE
                    binding.txtDeviceAction.visibility = View.VISIBLE
                    binding.spDeviceAction.visibility = View.VISIBLE
                }
                val selected: TextView = parent?.getChildAt(0) as TextView
                selected.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.spDeviceAction.onItemSelectedListener = object : OnItemSelectedListener {
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

        binding.btnCreateDevice.setOnClickListener {
            val name = binding.edDeviceName.text.toString().trim()
            val deviceType = DeviceType.valueOf(binding.spDeviceType.selectedItem.toString())
            var deviceAction = DeviceAction.valueOf(binding.spDeviceAction.selectedItem.toString())
            if (deviceType == DeviceType.MONITOR) {
                deviceAction = DeviceAction.NOTHING
            }
            val unitMeasure = binding.edUnitMeasure.text.toString().trim()

            viewModel.saveDevice(DeviceRequest(name, deviceType, deviceAction, unitMeasure))
        }

        viewModel.device.observe(viewLifecycleOwner) {
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
                        Log.d("error_api", it.toString())
                    }

                }
            }
        }
        customHeaderBar(binding)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun addSpinner(binding: FragmentCreateDeviceBinding) {
        val deviceTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spiner_dropdown,
            resources.getStringArray(R.array.deviceTypeList)
        )
        val deviceActionAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spiner_dropdown,
            resources.getStringArray(R.array.deviceActionList)
        )

        val deviceTypePosition: Int =
            deviceTypeAdapter.getPosition(resources.getStringArray(R.array.deviceTypeList)[0])
        val deviceActionPosition: Int =
            deviceActionAdapter.getPosition(resources.getStringArray(R.array.deviceActionList)[0])

        binding.spDeviceType.adapter = deviceTypeAdapter
        binding.spDeviceType.setSelection(deviceTypePosition)

        binding.spDeviceAction.adapter = deviceActionAdapter
        binding.spDeviceAction.setSelection(deviceActionPosition)
    }

    private fun customHeaderBar(binding: FragmentCreateDeviceBinding) {
        binding.vhCreateDevice.imvBack.setOnClickListener {
            if (findNavController().popBackStack().not()) {
                requireActivity().finish()
            }
        }
    }
}