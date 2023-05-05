package com.ldnhat.smarthomeapp.ui.device

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.FragmentDeviceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceFragment : Fragment() {
    private val viewModel by viewModels<DeviceViewModel>()

    private lateinit var deviceTimerAdapter: DeviceTimerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDeviceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_device, container, false)

        val device: DeviceResponse =
            DeviceFragmentArgs.fromBundle(requireArguments()).selectedDevice
        viewModel.getDeviceById(device.id)

        viewModel.device.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    is Resource.Success -> {
                        updateUI(it.value, binding)
                    }
                    else -> {}
                }

            }
        }

        if (device.deviceType == DeviceType.CONTROL) {
            setViewDeviceControl(binding)

            // get device action on firebase
            handleDeviceFirebase(device)

            // set device timers list to adapter
            handleDeviceTimer(device, binding)

            viewModel.btnActionDevice.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.onChangeActionDevice(device.id)
                    viewModel.selectedStateDeviceComplete()
                }
            }

        } else {
            setViewDeviceMonitor(binding)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceToHome())
        }

        customHeaderBar(binding)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun customHeaderBar(binding: FragmentDeviceBinding) {
        binding.vhDevice.imvBack.setOnClickListener {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceToHome())
        }
    }

    private fun handleDeviceTimer(
        device: DeviceResponse,
        binding: FragmentDeviceBinding
    ) {
        viewModel.getAllDeviceTimer(device.id)
        deviceTimerAdapter =
            DeviceTimerAdapter(DeviceTimerAdapter.OnClickListener { deviceTimer, position ->
                run {
                    viewModel.deleteDeviceTimer(deviceTimer.id)
                    when (val data = viewModel.deviceTimers.value) {
                        is Resource.Success -> {
                            viewModel.updateDeviceTimers(data.value.filter { s -> s.id != deviceTimer.id })
                        }
                        else -> {}
                    }

                    binding.rcDeviceTimer.adapter?.notifyItemRemoved(position)
                }
            })
        viewModel.deviceTimers.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    deviceTimerAdapter.submitList(it.value)
                }
                else -> {}
            }
        }

        binding.rcDeviceTimer.adapter = deviceTimerAdapter
    }

    private fun setViewDeviceControl(binding: FragmentDeviceBinding) {
        binding.txtDeviceAction.visibility = View.VISIBLE
        binding.txtDeviceActionValue.visibility = View.VISIBLE
        binding.dividerLayoutBottom.visibility = View.VISIBLE
        binding.cvButtonTurnDevice.visibility = View.VISIBLE
        binding.rcDeviceTimer.visibility = View.VISIBLE
    }

    private fun setViewDeviceMonitor(binding: FragmentDeviceBinding) {
        binding.txtDeviceAction.visibility = View.GONE
        binding.txtDeviceActionValue.visibility = View.GONE
        binding.dividerLayoutBottom.visibility = View.GONE
        binding.cvButtonTurnDevice.visibility = View.GONE
        binding.rcDeviceTimer.visibility = View.GONE
    }

    private fun handleDeviceFirebase(device: DeviceResponse) {
        val db = Firebase.firestore
        db.collection("develop").document("device_action").collection(device.createdBy)
            .document(device.id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("Firebase", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("Firebase", snapshot.data?.get("action").toString())
                    viewModel.onDeviceAction(snapshot.data?.get("action").toString())
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(device: DeviceResponse, binding: FragmentDeviceBinding) {
        with(binding) {
            txtDeviceName1.text = "[" + device.name + "]"
            txtDeviceNameValue.text = device.name
            txtDeviceTypeValue.text = device.deviceType.toString()
            txtCreatedByValue.text = device.createdBy
            txtCreatedDateValue.text = device.createDateConverter
            txtModifiedByValue.text = device.lastModifiedBy
            txtModifiedDateValue.text = device.lastModifiedDateConverter
        }
    }

}