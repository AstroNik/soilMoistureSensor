package com.example.soilmoisturesensor

import java.sql.Time

/**
 * Data class for Notification data
 * @author Manpreet Sandhu
 */
data class NotificationData (

    var notificationID:Int,
    var deviceID:Int,
    var dateTime:String,
    var title:String,
    var content:String,
    var isRead:Boolean,
    var deviceName : String


)