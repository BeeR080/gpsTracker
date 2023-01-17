package com.beer080.gpstracker.main.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.FragmentHomeBinding
import com.beer080.gpstracker.main.data.LocationService
import com.beer080.gpstracker.main.utils.DialogManager
import com.beer080.gpstracker.main.utils.TimeUtils
import com.beer080.gpstracker.main.utils.chekPermisson
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class HomeFragment : Fragment() {
private var isServiceRunning = false
    private var timer: Timer? = null
    private var startTime = 0L
    private val timeData = MutableLiveData<String>()
private lateinit var binding: FragmentHomeBinding
private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mapSettings()
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClicks()
        checkServiceState()
        updateTimeView()

    }

    override fun onResume() {
        super.onResume()
        checkLocPermission()
    }

    private fun mapSettings(){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initMap() = with(binding){
        map.controller.setZoom(20.0)
        val mLocation = GpsMyLocationProvider(activity)
        val mLocOverlay = MyLocationNewOverlay(mLocation,map)
        mLocOverlay.apply {
            enableMyLocation()
            enableFollowLocation()
            runOnFirstFix {
                map.overlays.clear()
                map.overlays.add(mLocOverlay)
            }
        }


    }

    private fun registerPermissions(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
           if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true){
               initMap()
               checkLocEnabled()
           }else{
               Toast.makeText(requireContext(),
                   requireContext().getString(R.string.permission_loc_disabled_message),
                   Toast.LENGTH_SHORT)
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
                    Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun checkLocEnabled(){
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isEnabled){
           DialogManager.showLocEnabledDialog(
               activity as AppCompatActivity,
               object : DialogManager.Listener{
                   override fun onClick() {
                       startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                   }

               }
           )
        } else{
            Toast.makeText(
                requireContext(),
                "Геолокация включена",
                Toast.LENGTH_SHORT)
                .show()

        }
    }
    private fun setOnClicks() = with(binding){
        val listener = onClicks()
        fbtStartRecTracks.setOnClickListener(listener)

    }

    private fun onClicks():OnClickListener{
        return OnClickListener {
            when(it.id){
                R.id.fbt_startRecTracks ->stopStartLocService()
            }
        }
    }

    private fun updateTimeView(){
timeData.observe(viewLifecycleOwner){
binding.hfTvTime.text = it
}
    }

    private fun startTimer(){
       timer?.cancel()
        timer = Timer()
        startTime = LocationService.serviceStarting
        timer?.schedule(object : TimerTask(){
            override fun run() {
               activity?.runOnUiThread {
                   timeData.value = getCurrentTime()
               }
            }

        }
            ,1000
            ,1000
        )
    }

    private fun getCurrentTime(): String{
        return "Time: ${TimeUtils.getTime(System.currentTimeMillis()- startTime)}"
    }
    private fun checkServiceState(){
        isServiceRunning = LocationService.isServiceRunnig
        if (isServiceRunning){
            binding.fbtStartRecTracks.setImageResource(R.drawable.ic_stoptracking)
            startTimer()

        }

    }

    private fun startLocService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity,LocationService::class.java))
        }else{
            activity?.startService(Intent(activity,LocationService::class.java))
        }
        binding.fbtStartRecTracks.setImageResource(R.drawable.ic_stoptracking)
        LocationService.serviceStarting = System.currentTimeMillis()
        startTimer()
    }


    private fun stopStartLocService(){
if(!isServiceRunning){
    startLocService()
}else{
    activity?.stopService(Intent(activity, LocationService::class.java))
    binding.fbtStartRecTracks.setImageResource(R.drawable.ic_starttracking)
    timer?.cancel()
}
        isServiceRunning = !isServiceRunning
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}


