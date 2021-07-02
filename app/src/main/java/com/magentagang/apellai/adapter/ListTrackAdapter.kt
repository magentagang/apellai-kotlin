package com.magentagang.apellai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magentagang.apellai.databinding.FragmentListTrackBinding
import com.magentagang.apellai.model.Track

class ListTrackAdapter(val clickListener: TrackListener) : ListAdapter<Track,
        ListTrackAdapter.ViewHolder>(GridTrackDiffCallback()) {
    class ViewHolder private constructor(val binding: FragmentListTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Track, clickListener: TrackListener) {
            binding.track = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentListTrackBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class GridTrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class TrackListener(val clickListener: (id: String) -> Unit) {
    fun onClick(track: Track) = clickListener(track.id)
}