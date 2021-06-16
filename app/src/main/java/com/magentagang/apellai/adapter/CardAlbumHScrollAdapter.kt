package com.magentagang.apellai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentCardAlbumBinding
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.RepositoryUtils

class CardAlbumHScrollAdapter() : ListAdapter<Album,
        CardAlbumHScrollAdapter.ViewHolder>(CardAlbumDiffCallback()) {

    class ViewHolder private constructor(val binding: FragmentCardAlbumBinding)
        : RecyclerView.ViewHolder(binding.root) {

        // glide related code
        private val glideOptions = RequestOptions()
            .fallback(R.drawable.placeholder_nocover)
            .diskCacheStrategy(DiskCacheStrategy.DATA)


        fun bind(item: Album) {
            binding.album = item
            // glide related code
            Glide.with(binding.root)
                .applyDefaultRequestOptions(glideOptions)
                .load(RepositoryUtils.getCoverArtUrl(item.coverArt!!))
                .placeholder(R.drawable.image_fill)
                .into(binding.albumArt)

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentCardAlbumBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    class CardAlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
