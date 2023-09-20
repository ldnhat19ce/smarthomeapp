package com.ldnhat.smarthomeapp.ui.notification_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.databinding.FragmentNotificationSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNotificationSettingBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_notification_setting,
                container,
                false
            )

//        requireActivity().onBackPressedDispatcher.addCallback {
//            findNavController().navigate(NotificationSettingFragmentDirections.actionNotificationSettingToDevice())
//        }

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}