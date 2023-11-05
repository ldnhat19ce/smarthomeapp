package com.ldnhat.smarthomeapp.ui.device

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.common.extensions.FilterDeviceModel

class DeviceFilterAdapter(val context: Context, var deviceFilterModel: List<FilterDeviceModel>) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return deviceFilterModel.size
    }

    override fun getItem(position: Int): Any {
        return deviceFilterModel[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: DeviceFilterViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_device_filter, parent, false)
            vh = DeviceFilterViewHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as DeviceFilterViewHolder
        }
        vh.name.text = deviceFilterModel[position].name
        return view
    }

    class DeviceFilterViewHolder(row: View) {
        val name: TextView = row.findViewById(R.id.filterName)

    }
}