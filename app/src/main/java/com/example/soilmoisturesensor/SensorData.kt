package com.example.soilmoisturesensor

//data class for sensor informations
data class SensorData(val airValue:Int, val dateTime:String, val soilMoisturePercent: Int, val soilMoistureValue:Int, val waterValue:Int, val sensorName:String)