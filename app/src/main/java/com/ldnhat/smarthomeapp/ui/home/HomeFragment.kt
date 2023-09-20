package com.ldnhat.smarthomeapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.rcDevice.adapter = DeviceGridAdapter(DeviceGridAdapter.OnClickListener {
            viewModel.displayDetailDetail(it)
        })

        viewModel.navigatedToSelectedDevice.observe(viewLifecycleOwner) {
            if (it != null) {
                this.findNavController().navigate(HomeFragmentDirections.actionToDeviceDetail(it))
                viewModel.displayDeviceDetailComplete()
            }
        }

        binding.homeViewModel = viewModel

        findNavController().saveState()
        customHeaderBar(binding)

        binding.addDevice.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionToSaveDevice())
        }

        binding.speechData.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToSpeechData())
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun customHeaderBar(binding: FragmentHomeBinding) {
        binding.vhHome.imvRightIcon.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionToProfile())
        }
    }
}