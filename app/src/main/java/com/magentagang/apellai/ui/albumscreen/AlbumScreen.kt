package com.magentagang.apellai.ui.albumscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentAlbumScreenBinding
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.util.RepositoryUtils

class AlbumScreen : Fragment() {
    private lateinit var binding: FragmentAlbumScreenBinding
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.placeholder_nocover)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    private lateinit var imageView: ImageView

    companion object {
        fun newInstance() = AlbumScreen()
    }

    private lateinit var albumScreenViewModel: AlbumScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application

        val albumScreenArgs: AlbumScreenArgs by navArgs()
        val albumId = albumScreenArgs.id

        val viewModelFactory = AlbumScreenViewModelFactory(application, albumId!!)
        albumScreenViewModel = ViewModelProvider(this, viewModelFactory).get(
            AlbumScreenViewModel::class.java
        )
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_album_screen, container, false
        )
        binding.lifecycleOwner = this
        binding.albumScreenViewModel = albumScreenViewModel
        imageView = binding.albumArtLarge
        binding.entityList.tag = albumId.toString()

        albumScreenViewModel.album.observe(viewLifecycleOwner, {
            // Following conditions are implemented like this because for
            //  some albums, the year info was "null" in UI
            if (it != null) {
                if (it.artist != "null") {
                    binding.albumArtistLarge.text = it.artist
                } else {
                    binding.albumArtistLarge.text = "Unknown Artist Name"
                }
                if (it.name != "null") {
                    binding.albumNameLarge.text = it.name
                } else {
                    binding.albumNameLarge.text = "Unknown Album Name"
                }
                if (it.year.toString() != "null") {
                    binding.albumArtistYear.text = it.year.toString()
                } else {
                    binding.albumArtistYear.text = "Unknown Year"
                }
                loadImage(it)
            }
        })

        return binding.root
    }

    private fun loadImage(album: Album) {
        Glide.with(this)
            .applyDefaultRequestOptions(glideOptions)
            .load(RepositoryUtils.getCoverArtUrl(album.coverArt!!))
            .placeholder(R.drawable.placeholder_nocover)
            .into(imageView)
    }


}


