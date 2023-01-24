package com.beer080.gpstracker.main.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.TracksItemBinding

class TracksAdapter : ListAdapter<TrackItem, TracksAdapter.TrackHolder>(Comporator()) {
    class TrackHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = TracksItemBinding.bind(view)

        fun bind(tracks: TrackItem) = with(binding) {
            tfTvdate.text = tracks.date
            tfTvvelocity.text = tracks.velocity + " km/h"
            tfTvtime.text = tracks.time
            tfTvdistance.text = tracks.distance +" km"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent,false)
        return TrackHolder(view)
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
}