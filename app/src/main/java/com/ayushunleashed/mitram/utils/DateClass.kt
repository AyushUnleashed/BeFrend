package com.ayushunleashed.mitram.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateClass() {
    lateinit var date: Date
    lateinit var currentDate: String
    lateinit var currentTime: String
    lateinit var currentTimeStamp:String

    public fun getTimeAndDate(): String {
        date= Calendar.getInstance().time
        currentDate=DateFormat.getDateInstance().format(date)
        currentTime =DateFormat.getTimeInstance().format(date.time)
        return currentTime+"\n"+currentDate
    }

    public fun getTime():String{
        date= Calendar.getInstance().time
        currentTime =DateFormat.getTimeInstance().format(date.time)
        return currentTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun getTimeStamp():String{
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        currentTimeStamp = formatted;
        return currentTimeStamp;
    }
}