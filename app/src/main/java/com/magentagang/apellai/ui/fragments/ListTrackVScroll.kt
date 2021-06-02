package com.magentagang.apellai.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.AlbumListener
import com.magentagang.apellai.adapter.ListAlbumAdapter
import com.magentagang.apellai.adapter.ListTrackAdapter
import com.magentagang.apellai.databinding.FragmentListAlbumVScrollBinding
import com.magentagang.apellai.databinding.FragmentListTrackVScrollBinding
import com.magentagang.apellai.viewmodel.ListAlbumViewModel
import com.magentagang.apellai.viewmodel.ListTrackViewModel
import com.magentagang.apellai.viewmodel.factory.ListAlbumViewModelFactory
import com.magentagang.apellai.viewmodel.factory.ListTrackViewModelFactory

//TODO same as ListAlbumVScroll
class ListTrackVScroll : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentListTrackVScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_track_v_scroll, container, false
        )

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListTrackViewModelFactory(application)
        val listTrackViewModel = ViewModelProvider(this, viewModelFactory).get(ListTrackViewModel::class.java)

        binding.listTrackViewModel = listTrackViewModel
        binding.lifecycleOwner = this


        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.trackVList.layoutManager = manager

        val adapter = ListTrackAdapter()
        binding.trackVList.adapter = adapter

        listTrackViewModel.tracks.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}