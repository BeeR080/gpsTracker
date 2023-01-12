package com.beer080.gpstracker.main.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.FragmentHomeBinding
import com.beer080.gpstracker.main.utils.DialogManager
import com.beer080.gpstracker.main.utils.chekPermisson
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class HomeFragment : Fragment() {

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

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}


