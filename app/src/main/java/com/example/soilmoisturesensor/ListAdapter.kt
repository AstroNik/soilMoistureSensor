package com.example.soilmoisturesensor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView


//Adapter which creates the view with the data received from webAPI

class ListAdapter(val context: Context, val list: SensorData) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false)

        val airValue = view.findViewById(R.id.airValue) as AppCompatTextView
        val dateTime = view.findViewById(R.id.dateTime) as AppCompatTextView
        val soilMoisturePercent = view.findViewById(R.id.soilMoisturePercent) as AppCompatTextView
        val soilMoistureValue = view.findViewById(R.id.soilMoistureValue) as AppCompatTextView
        val waterLevel = view.findViewById(R.id.waterLevel) as AppCompatTextView
        val sensorName = view.findViewById(R.id.sensorName) as AppCompatTextView

        var date = list.dateTime.replace("T".toRegex()," Time: ")
        date = date.replace("\\..*".toRegex(),"")

        airValue.text = "Air Value: " + list.airValue.toString()
        dateTime.text = "Date: "+ date
        soilMoisturePercent.text = "Soil Moisture Percent: "+ list.soilMoisturePercent.toString()
        soilMoistureValue.text = "Soil Moisture Value: "+ list.soilMoistureValue.toString()
        waterLevel.text = "Water Level: "+ list.waterValue.toString()
        sensorName.text = "Sensor Name: "+ list.sensorName
        return  view
    }

    override fun getItem(position: Int): Any {
        return list
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 1
    }
}



