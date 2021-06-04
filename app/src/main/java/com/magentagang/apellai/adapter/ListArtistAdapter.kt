package com.magentagang.apellai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magentagang.apellai.databinding.FragmentListArtistBinding
import com.magentagang.apellai.generated.callback.OnClickListener
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist

class ListArtistAdapter(val clickListener: ArtistListener) : ListAdapter<Artist,
        ListArtistAdapter.ViewHolder>(GridArtistDiffCallback()) {

    class ViewHolder private constructor(val binding: FragmentListArtistBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Artist, clickListener: ArtistListener) {
            binding.artist = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentListArtistBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class GridArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
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

class ArtistListener(val clickListener: (ID: String) -> Unit) {
    fun onClick(artist: Artist) = clickListener(artist.ID)
}