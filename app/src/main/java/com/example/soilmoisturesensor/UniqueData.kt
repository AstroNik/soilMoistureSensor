package com.example.soilmoisturesensor

/**
 * Data class for unique data
 * @author Ehsan kabir
 */
data class UniqueData(
    val deviceId: Int,
    val dateTime: String,
    val soilMoisturePercent: Int
)