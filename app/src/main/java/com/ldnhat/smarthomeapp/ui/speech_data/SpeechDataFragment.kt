package com.ldnhat.smarthomeapp.ui.speech_data

import android.os.Bundle
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
import com.ldnhat.smarthomeapp.databinding.FragmentSpeechDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpeechDataFragment : Fragment() {
    private val viewModel by viewModels<SpeechDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSpeechDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_speech_data, container, false)

        viewModel.getAllSpeechData()

        val speechDataAdapter =
            SpeechDataAdapter(SpeechDataAdapter.OnClickListener { speechData, position ->
                run {
                    viewModel.deleteSpeechData(speechData.id)
                    when (val data = viewModel.speechData.value) {
                        is Resource.Success -> {
                            viewModel.updateSpeechData(data.value.filter { s -> s.id != speechData.id })
                        }
                        else -> {}
                    }
                    binding.rcSpeechData.adapter?.notifyItemRemoved(position)
                }
            })

        viewModel.speechData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    speechDataAdapter.submitList(it.value)
                }
                else -> {}
            }
        }
        binding.rcSpeechData.adapter = speechDataAdapter

        binding.addSpeechData.setOnClickListener {
            findNavController().navigate(SpeechDataFragmentDirections.actionSpeechDataToSave())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(SpeechDataFragmentDirections.actionSpeechDataToHome())
        }

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}