package com.example.soilmoisturesensor

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_sensor_detail.*
import okhttp3.*
import java.io.IOException

import java.util.*


// Fetching data from web api and sending it to the adapter
class SensorDetail : AppCompatActivity() {

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private var channelId = "com.example.soilmoisturesensor"
    private var description = "Soil Moisture Sensor"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_detail)

        setNotification.setOnClickListener{
            val calender:Calendar = Calendar.getInstance()
            calender.set(Calendar.HOUR_OF_DAY, 18)
            calender.set(Calendar.MINUTE, 30)
            calender.set(Calendar.SECOND, 10)

            val intent:Intent = Intent(this,Notification_Receiver::class.java)
            val pendingIntent:PendingIntent = PendingIntent.getBroadcast(this, 100,intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager:AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calender.timeInMillis, AlarmManager.INTERVAL_HOUR, pendingIntent)

        }

        fetchJson()

    }

    //method to fetch JSON data from web api
    fun fetchJson(){
        //println("Attempting to fetch json")
        val URL = "http://ecoders.ca/getSensorData"

        val request = Request.Builder().url(URL).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                println(body)

                val gson = GsonBuilder().create()

                val sensorData = gson.fromJson(body, SensorData::class.java)

                runOnUiThread {
                    val adapter = ListAdapter(applicationContext, sensorData)
                    plant_detail.adapter = adapter
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e)
            }
        })
    }

    fun backToHome(view: View){
        finish()
    }

    fun refresh(view: View){
        finish()
        startActivity(Intent(applicationContext, SensorDetail::class.java))
    }



}