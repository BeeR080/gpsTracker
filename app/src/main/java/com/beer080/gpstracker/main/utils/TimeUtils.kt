package com.beer080.gpstracker.main.utils



import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val timeFormater  = SimpleDateFormat("HH:mm:ss")
    private val dateFormater  = SimpleDateFormat("dd/MM/yyyy HH:mm")

    fun getTime(timeInMilis: Long): String{
        val calendar = Calendar.getInstance()
        timeFormater.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = timeInMilis
        return timeFormater.format(calendar.time)
    }

    fun getDate():String{
        val calendar = Calendar.getInstance()
        return dateFormater.format(calendar.time)
    }
}