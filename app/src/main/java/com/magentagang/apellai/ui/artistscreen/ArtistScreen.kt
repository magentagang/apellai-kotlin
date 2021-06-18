package com.magentagang.apellai.ui.artistscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentArtistScreenBinding
import com.magentagang.apellai.ui.albumscreen.AlbumScreenViewModel

class ArtistScreen : Fragment() {
    private lateinit var binding: FragmentArtistScreenBinding
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.placeholder_nocover)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    private lateinit var imageView : ImageView
    companion object {
        fun newInstance() = ArtistScreen()
    }

    private lateinit var artistScreenViewModel: ArtistScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val application = requireNotNull(this.activity).application

        val artistScreenArgs: ArtistScreenArgs by navArgs()
        val artistId = artistScreenArgs.id

        val viewModelFactory = ArtistScreenViewModelFactory(application, artistId!!)
        artistScreenViewModel = ViewModelProvider(this, viewModelFactory).get(
            ArtistScreenViewModel::class.java
        )
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_artist_screen, container, false
        )
        binding.lifecycleOwner = this
        binding.artistScreenViewModel = artistScreenViewModel
        binding.fragment.tag = artistId
        artistScreenViewModel.artist.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.artistNameLarge.text = it.name
            }
        })
        return binding.root
    }
}