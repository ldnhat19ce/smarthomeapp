package com.ldnhat.smarthomeapp.ui.device

import android.annotation.SuppressLint
import android.graphics.Color
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

    private var lineDeviceMonitor = listOf<Pair<String, Float>>()

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
            viewModel.getAllDeviceMonitors(device.id)
            handleDeviceMonitorFirebase(device, binding)
            viewModel.deviceMonitors.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.value.isNotEmpty()) {
                            lineDeviceMonitor = it.value.map { Pair(it.value, it.value.toFloat()) }.toList()
                            binding.apply {
                                chartDeviceMonitor.gradientFillColors =
                                    intArrayOf(
                                        Color.parseColor("#33F0FF"),
                                        Color.TRANSPARENT
                                    )
                                chartDeviceMonitor.animation.duration = animationDuration

                                chartDeviceMonitor.animate(lineDeviceMonitor)
                            }
                        }
                    }
                    is Resource.Failure -> {}
                }
            }


        }

        binding.btnNotificationSetting.setOnClickListener {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceToNotificationSetting())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceToHome())
        }

        customHeaderBar(binding)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    companion object {
        private const val animationDuration = 1000L
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
        binding.chartDeviceMonitor.visibility = View.GONE
        binding.txtDeviceValue.visibility = View.GONE
        binding.txtDevicevValue.visibility = View.GONE
    }

    private fun setViewDeviceMonitor(binding: FragmentDeviceBinding) {
        binding.txtDeviceAction.visibility = View.GONE
        binding.txtDeviceActionValue.visibility = View.GONE
        binding.dividerLayoutBottom.visibility = View.GONE
        binding.cvButtonTurnDevice.visibility = View.GONE
        binding.rcDeviceTimer.visibility = View.GONE
        binding.chartDeviceMonitor.visibility = View.VISIBLE
        binding.txtDeviceValue.visibility = View.VISIBLE
        binding.txtDevicevValue.visibility = View.VISIBLE
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
    private fun handleDeviceMonitorFirebase(device: DeviceResponse, binding: FragmentDeviceBinding) {
        val db = Firebase.firestore
        db.collection("develop").document("device_monitor").collection(device.createdBy)
            .document(device.id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("Firebase", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("Firebase", snapshot.data?.get("value").toString())
                    binding.txtDevicevValue.text = snapshot.data?.get("value").toString() + " " + snapshot.data?.get("unitMeasure").toString()
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