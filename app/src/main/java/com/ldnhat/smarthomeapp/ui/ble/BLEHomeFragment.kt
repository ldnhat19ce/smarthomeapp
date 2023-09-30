package com.ldnhat.smarthomeapp.ui.ble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.databinding.FragmentBleHomeBinding

class BLEHomeFragment : Fragment() {
    private val viewModel by viewModels<BLEHomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding : FragmentBleHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble_home, container, false)

        binding.lifecycleOwner = this
        return binding.root
    }
}