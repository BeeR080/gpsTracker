package com.beer080.gpstracker.main.utils



import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val timeFormater  = SimpleDateFormat("HH:mm:ss")

    fun getTime(timeInMilis: Long): String{
        val calendar = Calendar.getInstance()
        timeFormater.timeZone = TimeZone.getTimeZone("UTC")
        calendar.timeInMillis = timeInMilis
        return timeFormater.format(calendar.time)
    }
}