package com.beer080.gpstracker.main.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.FragmentViewTrackBinding
import com.beer080.gpstracker.main.HomeViewModel
import com.beer080.gpstracker.main.MainApp
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class ViewTrackFragment : Fragment() {
    private lateinit var binding: FragmentViewTrackBinding
    private var startPoint: GeoPoint?=null

    private val model: HomeViewModel by activityViewModels {
        HomeViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapSettings()


        binding = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrack()
        binding.fbtFindeMe.setOnClickListener{
            binding.fvMap.controller.animateTo(startPoint)
        }
    }

    private fun mapSettings() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    @SuppressLint("SetTextI18n")
    private fun getTrack() = with(binding) {
        model.currentTrack.observe(viewLifecycleOwner) {
            fvTvDate.text = "Date: " + it.date
            fvTvTime.text = "Time: " + it.time
            fvTvAvgVelocity.text = "Velocity: " + it.velocity + " km/h"
            fvTvDistance.text = "Distance: " + it.distance + " km"
            val polyline = getPolyline(it.geoPoints)
            fvMap.overlays.add(polyline)
            setMarkers(polyline.actualPoints)
            goToStartPosition(polyline.actualPoints[0])
            startPoint = polyline.actualPoints[0]

        }
    }

    private fun goToStartPosition(startPosition: GeoPoint) {
        binding.fvMap.controller.apply {
            zoomTo(18.0)
            animateTo(startPosition)
        }

    }

    private fun getPolyline(geopoints: String): Polyline {
        val list = geopoints.split("/")
        val polyline = Polyline()
        polyline.outlinePaint.color = Color.parseColor(
            PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                    .getString("update_color_key", "#FF6200EE"))

        list.forEach {
            if (it.isEmpty()) return@forEach
            val points = it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
        }
        return polyline
    }

    private fun setMarkers(pointsList: List<GeoPoint>) = with(binding){
        val startMarker = Marker(fvMap)
        val endMarker = Marker(fvMap)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = getDrawable(requireContext(), R.drawable.ic_startposition)
        endMarker.icon = getDrawable(requireContext(), R.drawable.ic_finishposition)
        startMarker.position = pointsList[0]
        endMarker.position = pointsList[pointsList.size - 1]
        fvMap.overlays.add(startMarker)
        fvMap.overlays.add(endMarker)
    }

    companion object {

        @JvmStatic
        fun newInstance() = ViewTrackFragment()

    }
}