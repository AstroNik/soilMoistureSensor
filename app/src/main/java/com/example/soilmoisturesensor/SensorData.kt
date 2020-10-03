package com.example.soilmoisturesensor

import java.sql.Time

//data class for sensor informations
data class SensorData(val deviceId:Int,
                      val deviceName:String,
                      val battery:Int,
                      val dateTime:String,
                      val airValue:Int,
                      val waterValue:Int,
                      val soilMoistureValue:Int,
                      val soilMoisturePercent: Int)
