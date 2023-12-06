package com.ldnhat.smarthomeapp.ui.save_device_timer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.enumeration.DeviceAction
import com.ldnhat.smarthomeapp.common.utils.AppUtils
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.data.request.DeviceDTO
import com.ldnhat.smarthomeapp.data.request.DeviceTimerRequest
import com.ldnhat.smarthomeapp.data.response.DeviceResponse
import com.ldnhat.smarthomeapp.databinding.FragmentDeviceTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class SaveDeviceTimerFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private val viewModel by viewModels<SaveDeviceTimerViewModel>()
    private lateinit var binding: FragmentDeviceTimerBinding

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private var myDay = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0

    private var currentDate: LocalDateTime = LocalDateTime.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_device_timer, container, false)

        addSpinner(binding)

        binding.edTime.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(requireContext(), this@SaveDeviceTimerFragment, year, month, day)
            datePickerDialog.show()
        }

        val device: DeviceResponse =
            SaveDeviceTimerFragmentArgs.fromBundle(requireArguments()).selectedDevice

        val deviceDTO = DeviceDTO()
        deviceDTO.id = device.id

        binding.btnCreateDeviceTimer.setOnClickListener {
            val deviceAction = DeviceAction.valueOf(binding.spDeviceAction.selectedItem.toString())
            val offsetDateTime: OffsetDateTime =
                LocalDateTime.parse(binding.edTime.text.trim()).atOffset(ZoneOffset.UTC)
                    .minusHours(7)
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
            viewModel.saveDeviceTimer(
                DeviceTimerRequest(
                    offsetDateTime.format(formatter).toString(), deviceAction, deviceDTO
                )
            )
        }

        viewModel.deviceTimer.observe(viewLifecycleOwner) {
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

    private fun customHeaderBar(binding: FragmentDeviceTimerBinding) {
        binding.vhDeviceTimer.imvBack.setOnClickListener {
            if (findNavController().popBackStack().not()) {
                requireActivity().finish()
            }
        }
    }

    private fun addSpinner(binding: FragmentDeviceTimerBinding) {
        val deviceActionAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spiner_dropdown,
            resources.getStringArray(R.array.deviceActionList).copyOfRange(0, 2)
        )

        val deviceActionPosition: Int =
            deviceActionAdapter.getPosition(resources.getStringArray(R.array.deviceActionList)[0])

        binding.spDeviceAction.adapter = deviceActionAdapter
        binding.spDeviceAction.setSelection(deviceActionPosition)

        binding.spDeviceAction.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
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
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(), this@SaveDeviceTimerFragment, hour, minute,
            DateFormat.is24HourFormat(requireContext())
        )
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        var m = myMonth + 1
        if (m > 12) {
            m = 1
        }

        val date = LocalDateTime.parse(
            "$myYear-${AppUtils.additionalZero(m)}-${AppUtils.additionalZero(myDay)} ${
                AppUtils.additionalZero(
                    myHour
                )
            }:${AppUtils.additionalZero(myMinute)}",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )

        currentDate = date
        binding.edTime.text = date.toString()
    }
}