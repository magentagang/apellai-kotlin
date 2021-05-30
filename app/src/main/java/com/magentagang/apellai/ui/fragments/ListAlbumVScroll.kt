package com.magentagang.apellai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.ListAlbumAdapter
import com.magentagang.apellai.databinding.FragmentListAlbumVScrollBinding
import com.magentagang.apellai.viewmodel.ListAlbumViewModel
import com.magentagang.apellai.viewmodel.factory.ListAlbumViewModelFactory

class ListAlbumVScroll : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentListAlbumVScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_album_v_scroll, container, false
        )

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListAlbumViewModelFactory(application)
        val listAlbumViewModel = ViewModelProvider(this, viewModelFactory).get(ListAlbumViewModel::class.java)

        binding.listAlbumViewModel = listAlbumViewModel
        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.albumVList.layoutManager = manager

        val adapter = ListAlbumAdapter()
        binding.albumVList.adapter = adapter

        listAlbumViewModel.albums.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}