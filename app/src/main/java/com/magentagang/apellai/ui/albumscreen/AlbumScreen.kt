package com.magentagang.apellai.ui.albumscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentAlbumScreenBinding
import com.magentagang.apellai.databinding.FragmentSearchBinding
import com.magentagang.apellai.ui.home.HomeViewModel
import com.magentagang.apellai.ui.search.SearchViewModel
import com.magentagang.apellai.ui.search.SearchViewModelFactory

class AlbumScreen : Fragment() {
    private lateinit var binding: FragmentAlbumScreenBinding
    companion object {
        fun newInstance() = AlbumScreen()
    }
    //I added it again this time
    private lateinit var viewModel: AlbumScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = AlbumScreenViewModelFactory(application)
        val albumScreenViewModel = ViewModelProvider(this, viewModelFactory).get(
            AlbumScreenViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_album_screen, container, false
        )
        binding.albumScreenViewModel = albumScreenViewModel
        binding.lifecycleOwner = this

        //TODO( Do all glide related work here) ->
        val ivImg = binding.albumArtLarge
        Glide.with(this)
            .load("http://via.placeholder.com/300.png")
            .placeholder(R.drawable.image_fill)
            .into(ivImg);

        return binding.root
    }
}

