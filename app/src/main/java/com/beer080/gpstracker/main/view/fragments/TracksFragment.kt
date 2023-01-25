package com.beer080.gpstracker.main.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.beer080.gpstracker.databinding.FragmentTracksBinding
import com.beer080.gpstracker.main.HomeViewModel
import com.beer080.gpstracker.main.MainApp
import com.beer080.gpstracker.main.data.TrackItem
import com.beer080.gpstracker.main.data.TracksAdapter
import com.beer080.gpstracker.main.utils.openFragment

class TracksFragment : Fragment(), TracksAdapter.Listener {


private lateinit var binding:FragmentTracksBinding
private lateinit var tracksAdapter:TracksAdapter
    private val model: HomeViewModel by activityViewModels{
        HomeViewModel.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentTracksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        getTracks()
    }


    private fun initRecyclerView() = with(binding){
        tracksAdapter = TracksAdapter(this@TracksFragment)
        rcvTracks.layoutManager = LinearLayoutManager(requireContext())
        rcvTracks.adapter = tracksAdapter

    }
    fun getTracks(){
        model.tracksList.observe(viewLifecycleOwner){
            tracksAdapter.submitList(it)
            binding.ftTvempty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        }
    }



    companion object {
        @JvmStatic
        fun newInstance() = TracksFragment()


    }

    override fun onClick(track: TrackItem, type: TracksAdapter.ClickType) {
        when(type){
            TracksAdapter.ClickType.DELETE->model.deleteTrack(track)
            TracksAdapter.ClickType.OPEN->{
                model.currentTrack.value = track
                openFragment(ViewTrackFragment.newInstance())
        }
        }

        Log.d("MyLog", "Type: $type")
    }



}


