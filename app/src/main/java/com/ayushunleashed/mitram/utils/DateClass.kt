package com.ayushunleashed.mitram.utils

import android.util.Log
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.text.DateFormat
import java.util.*

class DateClass() {
    lateinit var date: Date
    lateinit var currentDate: String
    lateinit var currentTime: String

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
}