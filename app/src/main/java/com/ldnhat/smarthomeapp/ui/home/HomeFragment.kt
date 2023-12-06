package com.ldnhat.smarthomeapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private var device : DeviceResponse = DeviceResponse()

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

        viewModel.devices.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.any { v -> v.deviceType == DeviceType.MONITOR }) {
                        val deviceInit = it.value.filter { v -> v.deviceType == DeviceType.MONITOR }[0]
                        var modifiedDate : String = deviceInit.lastModifiedDateConverter
                        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("vi", "VN"))
                        val date = formatter.parse(modifiedDate)?.toInstant() ?: Date().toInstant()

                        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale("us", "VN"))
                            .withZone(ZoneOffset.UTC)

                        modifiedDate = dateTimeFormatter.format(date)
                        viewModel.setLastModifiedDate(modifiedDate)
                        viewModel.getDeviceMonitorRange(deviceInit)

                        val db = Firebase.firestore
                        db.collection("develop").document("device_monitor")
                            .collection(deviceInit.createdBy)
                            .document(deviceInit.id)
                            .addSnapshotListener{snapshot, e ->
                                if (e != null) {
                                    Log.d("Firebase", "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    viewModel.setCurrentDeviceMonitorValue("${AppUtils.formatDeviceValue(snapshot.data?.get("value").toString())}${deviceInit.unitMeasure}")
                                }
                            }
                        viewModel.setDeviceInit(deviceInit)
                    }
                }
                else -> {}
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

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun customHeaderBar(binding: FragmentHomeBinding) {
        binding.vhHome.imvRightIcon.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionToProfile())
        }
    }
}