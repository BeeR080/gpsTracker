package com.beer080.gpstracker.main.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.TracksItemBinding

class TracksAdapter(private val listener:Listener) : ListAdapter<TrackItem, TracksAdapter.TrackHolder>(Comporator()) {


    class TrackHolder(view: View, private val listener: Listener): RecyclerView.ViewHolder(view) , View.OnClickListener{
        private val binding = TracksItemBinding.bind(view)
        private var trackTemp: TrackItem?=null

        init {
            binding.tfBtndelete.setOnClickListener(this)
            binding.trackList.setOnClickListener(this)
        }

        fun bind(tracks: TrackItem) = with(binding) {
            trackTemp = tracks
            tfTvdate.text = tracks.date
            tfTvvelocity.text = tracks.velocity + " km/h"
            tfTvtime.text = tracks.time
            tfTvdistance.text = tracks.distance +" km"
        }

        override fun onClick(view: View) {
           val type = when (view.id){
                R.id.tf_btndelete -> ClickType.DELETE
                R.id.track_list -> ClickType.OPEN
                else -> ClickType.OPEN
            }
            trackTemp?.let { listener.onClick(it,type) }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent,false)
        return TrackHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
      holder.bind(getItem(position))
    }



    class Comporator : DiffUtil.ItemCallback<TrackItem>(){
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener{
        fun onClick(track:TrackItem, type:ClickType)
    }
    enum class ClickType{
        DELETE,
        OPEN
    }

}
