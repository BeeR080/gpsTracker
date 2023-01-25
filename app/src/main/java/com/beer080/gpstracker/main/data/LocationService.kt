package com.beer080.gpstracker.main.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.beer080.gpstracker.R
import com.beer080.gpstracker.main.view.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import org.osmdroid.util.GeoPoint

class LocationService: Service() {

    private lateinit var  locProvider: FusedLocationProviderClient
    private lateinit var  locRequest: LocationRequest
    private var lastLocation: Location? = null
    private var distance = 0F
    private lateinit var geoPointsList: ArrayList<GeoPoint>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        startLocationUpdates()
        isServiceRunnig = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLoc()

    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunnig = false
        locProvider.removeLocationUpdates(locCallBack)
    }

    private val locCallBack = object : LocationCallback(){
        override fun onLocationResult(locResult: LocationResult) {
            super.onLocationResult(locResult)
            val currentLoc = locResult.lastLocation
            if(lastLocation != null && currentLoc != null){

                   distance+= lastLocation?.distanceTo(currentLoc ?: lastLocation) ?: 0.0f
                    geoPointsList.add(GeoPoint(currentLoc.latitude,currentLoc.longitude))

                    val locModel = LocationModel(
                        currentLoc.speed,
                        distance,
                        geoPointsList
                        )
                sendLocData(locModel)
            }

            lastLocation = currentLoc
        }
    }

    private fun sendLocData(locModel: LocationModel){
        val intent = Intent(LOC_MODEL_INTENT)
        intent.putExtra(LOC_MODEL_INTENT, locModel)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
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

    private fun initLoc(){
        val updateInterval = PreferenceManager.getDefaultSharedPreferences(
            this,

        ).getString("update_time_key","3000")?.toLong() ?:3000
        locRequest = LocationRequest.create()
            .apply {
                this.interval = updateInterval
                this.fastestInterval = updateInterval
                this.priority = PRIORITY_HIGH_ACCURACY
        }
        locProvider = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locProvider.requestLocationUpdates(
        locRequest,
            locCallBack,
            Looper.myLooper()
        )
    }

    companion object{
        const val LOC_MODEL_INTENT = "loc_intent"
        const val CHANNEL_ID = "channel_1"
        var isServiceRunnig = false
        var serviceStarting = 0L
    }
}

