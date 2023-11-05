package com.ldnhat.smarthomeapp.ui.device

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.enumeration.DeviceType
import com.ldnhat.smarthomeapp.common.extensions.FilterDeviceModel
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.response.DeviceMonitorResponse
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

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

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
            val aaChartModel: AAChartModel = AAChartModel()
                .chartType(AAChartType.Area)
                .title("${device.name} Line Chart")
                .axesTextColor("#FFFFFF")
                .yAxisGridLineWidth(0)
                .dataLabelsEnabled(true)
                .titleStyle(AAStyle().color("#FFFFFF"))
                .animationType(AAChartAnimationType.BouncePast)
                .zoomType(AAChartZoomType.XY)
                .backgroundColor(Color.TRANSPARENT)
                .gradientColorEnable(true)
                .yAxisMax(60)
                .series(arrayOf(AASeriesElement().name(device.name).data(arrayOf(0))))

            binding.aaChartView.aa_drawChartWithChartModel(aaChartModel)

            setViewDeviceMonitor(binding)
            handleDeviceMonitorFirebase(device, binding)
            viewModel.deviceMonitors.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val list: List<DeviceMonitorResponse> = response.value
//                        lineDeviceMonitor = if(list.isEmpty()) {
//                            listOf(Pair("0", 0F))
//                        } else {
//                            list.map { deviceMonitor -> Pair(deviceMonitor.value, deviceMonitor.value.toFloat()) }.toList()
//                        }

                        binding.aaChartView
                            .aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                                arrayOf(
                                    AASeriesElement()
                                        .data(list.map { deviceMonitor -> deviceMonitor.value.toFloat() }
                                            .toTypedArray())
                                )
                            )


                    }
                    is Resource.Failure -> {}
                }
            }

            val deviceFilterList: List<FilterDeviceModel> = readDeviceFilter()
            val deviceFilterAdapter = DeviceFilterAdapter(requireContext(), deviceFilterList)
            val filterAdapter = binding.filterDeviceMonitor
            filterAdapter.adapter = deviceFilterAdapter
            filterAdapter.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected: ConstraintLayout = parent?.getChildAt(0) as ConstraintLayout
                    val textView: TextView = selected.getViewById(R.id.filterName) as TextView
                    textView.setTextColor(Color.WHITE)
                    val item: FilterDeviceModel =
                        filterAdapter.adapter.getItem(position) as FilterDeviceModel
                    viewModel.getAllDeviceMonitors(device.id, item.type)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        }

        binding.btnNotificationSetting.setOnClickListener {
            findNavController().navigate(DeviceFragmentDirections.actionDeviceToNotificationSetting())
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
            if (findNavController().popBackStack().not()) {
                requireActivity().finish()
            }
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
//        binding.chartDeviceMonitor.visibility = View.GONE
        binding.txtDeviceValue.visibility = View.GONE
        binding.txtDevicevValue.visibility = View.GONE
        binding.circleDeviceMonitor.visibility = View.GONE
        binding.filterDeviceMonitor.visibility = View.GONE
    }

    private fun setViewDeviceMonitor(binding: FragmentDeviceBinding) {
        binding.txtDeviceAction.visibility = View.GONE
        binding.txtDeviceActionValue.visibility = View.GONE
        binding.dividerLayoutBottom.visibility = View.GONE
        binding.cvButtonTurnDevice.visibility = View.GONE
        binding.rcDeviceTimer.visibility = View.GONE
//        binding.chartDeviceMonitor.visibility = View.GONE
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
    private fun handleDeviceMonitorFirebase(
        device: DeviceResponse,
        binding: FragmentDeviceBinding
    ) {
        val db = Firebase.firestore
        db.collection("develop").document("device_monitor").collection(device.createdBy)
            .document(device.id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("Firebase", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("Firebase", snapshot.data?.get("value").toString())
                    binding.circleDeviceMonitor.progress =
                        snapshot.data?.get("value").toString().toFloat()
                    viewModel.setCurrentValue(snapshot.data?.get("value").toString() + "°C")
                    binding.txtDevicevValue.text = snapshot.data?.get("value")
                        .toString() + " " + snapshot.data?.get("unitMeasure").toString()
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

    private fun readDeviceFilter(): List<FilterDeviceModel> {
        val fileName = "dropdown_list.json"
        val bufferReader = requireContext().assets.open(fileName).bufferedReader()
        val jsonString = bufferReader.use { it.readText() }
        return Gson().fromJson(jsonString, Array<FilterDeviceModel>::class.java).toList()
    }

}