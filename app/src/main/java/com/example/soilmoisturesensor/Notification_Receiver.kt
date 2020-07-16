package com.example.soilmoisturesensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews


class Notification_Receiver: BroadcastReceiver(){

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private var channelId = "com.example.soilmoisturesensor"
    private var description = "Soil Moisture Sensor"

    override fun onReceive(context: Context?, intent: Intent?) {

        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, SensorDetail::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val contentView = RemoteViews(context?.packageName, R.layout.notification_layout)
        contentView.setTextViewText(R.id.notification_title, "Soil Moisture Sensor")
        contentView.setTextViewText(R.id.notification_content, "Check moisture level")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
        }else{
            builder = Notification.Builder(context)
                .setContent(contentView)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())

    }
}