package com.magentagang.apellai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentListTrackSearchBinding
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.util.RepositoryUtils

class ListTrackSearchAdapter(val clickListener: TrackListener) : ListAdapter<Track,
        ListTrackSearchAdapter.ViewHolder>(GridTrackDiffCallback()) {
    class ViewHolder private constructor(val binding: FragmentListTrackSearchBinding)
        : RecyclerView.ViewHolder(binding.root) {

        private val glideOptions = RequestOptions()
            .fallback(R.drawable.placeholder_nocover)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

        fun bind(item: Track, clickListener: TrackListener) {
            binding.track = item
            binding.clickListener = clickListener
            Glide.with(binding.root)
                .applyDefaultRequestOptions(glideOptions)
                .load(RepositoryUtils.getCoverArtUrl(item.coverArt!!))
                .placeholder(R.drawable.placeholder_nocover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.trackArt)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentListTrackSearchBinding.inflate(layoutInflater, parent, false)
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