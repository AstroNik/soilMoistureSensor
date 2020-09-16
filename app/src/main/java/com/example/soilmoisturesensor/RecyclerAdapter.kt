package com.example.soilmoisturesensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dashboard_data_card.view.*
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_deviceName
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_moistureLevel
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_updateTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<SensorData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.dashboard_data_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(list: ArrayList<SensorData>) {
        items = list
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName = itemView.textView_deviceName
        val dateTime = itemView.textView_updateTime
        val soilMoisturePercent = itemView.textView_moistureLevel
        val deviceId = itemView.textView_deviceId
        val batteryLevel = itemView.textView_batteryLevel

        fun bind(sensorData: SensorData) {
            deviceName.text = "Device name: " + sensorData.deviceName
            dateTime.text = "Last updated: " + formatDate(sensorData.dateTime)
            soilMoisturePercent.text = sensorData.soilMoisturePercent.toString()
            deviceId.text = "ID: " + sensorData.deviceId.toString()
            batteryLevel.text = sensorData.battery.toString()
        }

        fun formatDate(date: String): String {

            var deviceDate = date.replace("\\..*".toRegex(), "")

            val date = deviceDate.substring(0, deviceDate.indexOf("T"))
            var time = deviceDate.substring(deviceDate.indexOf("T") + 1).trim()

            val year = date.substring(0, date.indexOf("-"))
            val month = date.substring(date.indexOf("-") + 1, date.indexOf("-") + 3)
            val day = date.substring(date.indexOf("-") + 4, date.indexOf("-") + 6)

            val hour = time.substring(0, time.indexOf(":"))
            val min = time.substring(time.indexOf(":") + 1, time.indexOf(":") + 3)

            time = convertToTwelvehour(hour + ":" + min)

            return time + " " + day + "/" + month + "/" + year
        }

        fun convertToTwelvehour(time: String): String {
            val code12Hours = SimpleDateFormat("hh:mm") // 12 hour format
            var dateCode12: Date? = null
            var formatTwelve: String
            var results: String

            try {
                dateCode12 = code12Hours.parse(time) // 12 hour
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            formatTwelve = code12Hours.format(dateCode12) // 12
            if (formatTwelve == time) {
                results = formatTwelve + " AM"
            } else {
                results = formatTwelve + " PM"
            }
            return results
        }
    }
}