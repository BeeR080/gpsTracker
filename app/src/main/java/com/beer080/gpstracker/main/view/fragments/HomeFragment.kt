package com.beer080.gpstracker.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beer080.gpstracker.databinding.FragmentHomeBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class HomeFragment : Fragment() {
private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mapSettings()
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
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

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}


