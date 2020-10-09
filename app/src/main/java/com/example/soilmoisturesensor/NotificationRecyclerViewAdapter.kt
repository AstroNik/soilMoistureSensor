package com.example.soilmoisturesensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item.view.*

class NotificationRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<NotificationData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotificationRecyclerViewAdapter.ViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(list: ArrayList<NotificationData>) {
        items = list
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alert = itemView.text_view_heading
        val message = itemView.text_view_message
        val date = itemView.text_view_time

        fun bind(notificationData: NotificationData) {
            alert.text = notificationData.title
            message.text = notificationData.content
            date.text = formatDate(notificationData.dateTime)
        }
    }

    fun formatDate(date: String): String {
        var deviceDate = date.replace("\\..*".toRegex(), "")
        deviceDate = deviceDate.replace("T".toRegex(), " ")
        deviceDate = deviceDate.toDate().formatTo("dd MMM yyyy h:mm a")
        return deviceDate
    }
}


