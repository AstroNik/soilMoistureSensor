package com.example.soilmoisturesensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dashboard_data_card.view.*
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_deviceName
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_moistureLevel
import kotlinx.android.synthetic.main.dashboard_data_card.view.textView_updateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter(private val listener: onItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val deviceName = itemView.textView_deviceName
        val dateTime = itemView.textView_updateTime
        val soilMoisturePercent = itemView.textView_moistureLevel
        val deviceId = itemView.textView_deviceId
        val batteryLevel = itemView.textView_batteryLevel

        fun bind(sensorData: SensorData) {
//            deviceName.text = "Device name: " + sensorData.deviceName
            dateTime.text = "Last updated: " + formatDate(sensorData.dateTime)
            soilMoisturePercent.text = sensorData.soilMoisturePercent.toString()
            deviceId.text = "ID: " + sensorData.deviceId.toString()
            batteryLevel.text = sensorData.battery.toString()
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        fun formatDate(date: String): String {
            var deviceDate = date.replace("\\..*".toRegex(), "")
            deviceDate = deviceDate.replace("T".toRegex(), " ")
            deviceDate = deviceDate.toDate().formatTo("dd MMM yyyy h:mm a")
            return deviceDate
        }

        fun String.toDate(
            dateFormat: String = "yyyy-MM-dd HH:mm:ss",
            timeZone: TimeZone = TimeZone.getTimeZone("UTC")
        ): Date {
            val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
            parser.timeZone = timeZone
            return parser.parse(this)
        }

        fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
}