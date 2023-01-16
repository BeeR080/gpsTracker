package com.beer080.gpstracker.main.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.beer080.gpstracker.R
import com.beer080.gpstracker.main.view.MainActivity

class LocationService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        isServiceRunnig = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunnig = false
    }

    private fun startNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
               CHANNEL_ID,
                "LocService",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            10,
            notificationIntent,
            0
            )
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Tracker Running!")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(99,notification)
    }
    companion object{
        const val CHANNEL_ID = "channel_1"
        var isServiceRunnig = false
    }
}

