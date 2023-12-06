package com.ldnhat.smarthomeapp.ui.notification_setting

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
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.request.DeviceDTO
import com.ldnhat.smarthomeapp.data.request.NotificationSettingRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.FragmentNotificationSettingBinding
import com.ldnhat.smarthomeapp.ui.save_device_timer.SaveDeviceTimerFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingFragment : Fragment() {
    private val viewModel by viewModels<NotificationSettingViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentNotificationSettingBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_notification_setting,
                container,
                false
            )

        val device: DeviceResponse =
            SaveDeviceTimerFragmentArgs.fromBundle(requireArguments()).selectedDevice

        val deviceDTO = DeviceDTO()
        deviceDTO.id = device.id

        binding.btnCreateNotificationSetting.setOnClickListener {
            val title = binding.edTitle.text.toString().trim()
            val message = binding.edMessage.text.toString().trim()
            val value = binding.edValue.text.toString().trim()

            viewModel.saveNotificationSetting(NotificationSettingRequest(title, message, value, deviceDTO))
        }

        viewModel.notificationSetting.observe(viewLifecycleOwner) {
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

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

        customHeaderBar(binding)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun customHeaderBar(binding: FragmentNotificationSettingBinding) {
        binding.vhNotificationSetting.imvBack.setOnClickListener {
            if (findNavController().popBackStack().not()) {
                requireActivity().finish()
            }
        }
    }
}