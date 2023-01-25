package com.beer080.gpstracker.main.view.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.FragmentHomeBinding
import com.beer080.gpstracker.main.HomeViewModel
import com.beer080.gpstracker.main.MainApp
import com.beer080.gpstracker.main.data.LocationModel
import com.beer080.gpstracker.main.data.LocationService
import com.beer080.gpstracker.main.data.TrackItem
import com.beer080.gpstracker.main.utils.DialogManager
import com.beer080.gpstracker.main.utils.TimeUtils
import com.beer080.gpstracker.main.utils.chekPermisson
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var mLocOverlay: MyLocationNewOverlay
    private var locModel: LocationModel? = null
    private var polyline: Polyline? = null
    private var firstStart = true
    private var isServiceRunning = false
    private var timer: Timer? = null
    private var startTime = 0L
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>


    private val model: HomeViewModel by activityViewModels {
        HomeViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapSettings()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
        updateTimeView()
        registerLocReceiver()
        locationUpdates()


    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()
    }

    private fun mapSettings() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initMap() = with(binding) {
        polyline = Polyline()
        polyline?.outlinePaint?.color = Color.parseColor(
            PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getString("update_color_key", "#FF6200EE"))

        map.controller.setZoom(20.0)
        val mLocation = GpsMyLocationProvider(activity)
        mLocOverlay = MyLocationNewOverlay(mLocation, map)
        mLocOverlay.apply {
            enableMyLocation()
            enableFollowLocation()
            runOnFirstFix {
                map.overlays.clear()
                map.overlays.add(mLocOverlay)
                map.overlays.add(polyline)
            }
        }


    }

    private fun registerPermissions() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    initMap()
                    checkLocEnabled()
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.permission_loc_disabled_message),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    private fun checkLocPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionAfter10()


        } else {
            checkPermissionBefore10()
        }
    }

    private fun checkPermissionAfter10() {
        if (chekPermisson(Manifest.permission.ACCESS_FINE_LOCATION)
            && chekPermisson(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initMap()
            checkLocEnabled()
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkPermissionBefore10() {
        if (chekPermisson(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            initMap()
            checkLocEnabled()
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun checkLocEnabled() {
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            DialogManager.showLocEnabledDialog(
                activity as AppCompatActivity,
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                    }

                }
            )
        } else {
            Toast.makeText(
                requireContext(),
                "Геолокация включена",
                Toast.LENGTH_SHORT
            )
                .show()

        }
    }

    private fun setOnClicks() = with(binding) {
        val listener = onClicks()
        fbtStartRecTracks.setOnClickListener(listener)
        fbtNavMe.setOnClickListener(listener)

    }

    private fun onClicks(): OnClickListener {
        return OnClickListener {
            when (it.id) {
                R.id.fbt_startRecTracks -> stopStartLocService()
                R.id.fbt_NavMe -> findMyMe()
            }
        }
    }

    private fun findMyMe(){
     binding.map.controller.animateTo(mLocOverlay.myLocation)
        mLocOverlay.enableFollowLocation()
    }

    private fun locationUpdates() = with(binding) {
        model.locationUpdates.observe(viewLifecycleOwner) {
            val distance = "Distance: ${String.format("%.1f", it.distance)} m"
            val velocity = "Velocity: ${String.format("%.1f", 3.6f * it.velocity)} km/h"
            val avrgVelocity = "Average Velocity: ${getAverageSpeed(it.distance)} km/h"
            hfTvDistance.text = distance
            hfTvVelocity.text = velocity
            hfTvAvgVelocity.text = avrgVelocity
            locModel = it
            updatePolyline(it.geoPointList)
        }
    }

    private fun updateTimeView() {
        model.timeData.observe(viewLifecycleOwner) {
            binding.hfTvTime.text = it
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.serviceStarting
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    model.timeData.value = getCurrentTime()
                }
            }

        }, 1000, 1000
        )
        binding.fhDateList.visibility = View.VISIBLE
    }

    private fun getCurrentTime(): String {
        return TimeUtils.getTime(System.currentTimeMillis() - startTime)
    }

    private fun geoPointsToString(list: List<GeoPoint>): String {
        val stringBuilder = StringBuilder()
        list.forEach {
            stringBuilder.append("${it.latitude}, ${it.longitude}/")
        }
        Log.d("MyLog", "Points: $stringBuilder")
        return stringBuilder.toString()
    }

    private fun checkServiceState() {
        isServiceRunning = LocationService.isServiceRunnig
        if (isServiceRunning) {
            binding.fbtStartRecTracks.setImageResource(R.drawable.ic_stoptracking)
            startTimer()

        }

    }

    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))
        }
        binding.fbtStartRecTracks.setImageResource(R.drawable.ic_stoptracking)
        LocationService.serviceStarting = System.currentTimeMillis()
        startTimer()
    }

    private fun stopStartLocService() {
        if (!isServiceRunning) {
            startLocService()
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.fbtStartRecTracks.setImageResource(R.drawable.ic_starttracking)
            timer?.cancel()
            binding.fhDateList.visibility = View.GONE
            val trackList = getTrackItem()
            DialogManager.showSaveDialog(
                requireContext(),
                trackList,
                object : DialogManager.Listener {

                    override fun onClick() {
                        model.addTracks(trackList)
                    }

                })
        }
        isServiceRunning = !isServiceRunning
    }

    private fun getTrackItem(): TrackItem {
        return TrackItem(
            null,
            getCurrentTime(),
            TimeUtils.getDate(),
            String.format("%.1f", locModel?.distance?.div(1000) ?: 0),
            getAverageSpeed(locModel?.distance ?: 0.0f),
            geoPointsToString(locModel?.geoPointList ?: listOf())

        )
    }

    private fun getAverageSpeed(distance: Float): String {

        return String.format(
            "%.1f",
            3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000.0f))
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.LOC_MODEL_INTENT) {
                val locModel =
                    intent.getSerializableExtra(LocationService.LOC_MODEL_INTENT) as LocationModel
                model.locationUpdates.value = locModel
                Log.d("MyLog", "Distance: ${locModel.distance}")
            }
        }
    }

    private fun registerLocReceiver() {
        val locFilter = IntentFilter(LocationService.LOC_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiver, locFilter)
    }

    private fun addPoint(list: List<GeoPoint>) {
        polyline?.addPoint(list[list.size - 1])

    }

    private fun fillPolyline(list: List<GeoPoint>) {
        list.forEach {
            polyline?.addPoint(it)
        }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        if (list.size > 1 && firstStart) {
            fillPolyline(list)
            firstStart = false
        } else {
            addPoint(list)
        }
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiver)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}


