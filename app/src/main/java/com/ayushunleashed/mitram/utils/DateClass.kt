package com.ayushunleashed.mitram.utils

import android.os.Build
import androidx.annotation.RequiresApi
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

    fun formatMonth(month: String?): String? {
        val monthParse = SimpleDateFormat("MM")
        val monthDisplay = SimpleDateFormat("MMM")
        return monthDisplay.format(monthParse.parse(month))
    }

    fun convertDate(date:String):String{
        var dateArray = date.split('-');
        var year = dateArray[0];
        var month = dateArray[1];
        var day = dateArray[2];

        if(day=="1"){
            day="1st"
        }else if(day=="2"){
            day="2nd"
        }else if(day=="3"){
            day="3rd"
        }else if(day=="31"){
            day="31st"
        }else{
            day=day+"th"
        }
        return (day+" "+formatMonth(month))
    }

    fun convertTime(time:String):String{

//        val time = "3:30 PM"

        val date12Format = SimpleDateFormat("hh:mm a")

        val date24Format = SimpleDateFormat("HH:mm")

        return date12Format.format(date24Format.parse(time))
    }
}