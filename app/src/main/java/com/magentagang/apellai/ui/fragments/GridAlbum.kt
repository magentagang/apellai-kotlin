package com.magentagang.apellai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.GridAlbumAdapter
import com.magentagang.apellai.databinding.FragmentGridAlbumBinding
import com.magentagang.apellai.viewmodel.GridAlbumViewModel
import com.magentagang.apellai.viewmodel.factory.GridAlbumViewModelFactory

class GridAlbum : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentGridAlbumBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_grid_album, container, false
        )

        val application = requireNotNull(this.activity).application
        val viewModelFactory = GridAlbumViewModelFactory(application)
        val gridAlbumViewModel = ViewModelProvider(this, viewModelFactory).get(GridAlbumViewModel::class.java)

        binding.gridAlbumViewModel = gridAlbumViewModel
        binding.lifecycleOwner = this

        val manager = GridLayoutManager(activity, 3)
        binding.albumGrid.layoutManager = manager

        val adapter = GridAlbumAdapter()
        binding.albumGrid.adapter = adapter

        gridAlbumViewModel.albums.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}