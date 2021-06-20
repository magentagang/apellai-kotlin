package com.magentagang.apellai.ui.fragments

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.ListTrackAdapter
import com.magentagang.apellai.databinding.FragmentListTrackVScrollBinding
import com.magentagang.apellai.viewmodel.ListTrackViewModel
import com.magentagang.apellai.viewmodel.factory.ListTrackViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.magentagang.apellai.adapter.TrackListener
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.ui.albumscreen.AlbumScreenDirections
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModel
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModelFactory

//TODO same as ListAlbumVScroll
class ListTrackVScroll : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentListTrackVScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_track_v_scroll, container, false
        )
        val albumId = container?.tag.toString()
        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListTrackViewModelFactory(application, albumId)
        val listTrackViewModel = ViewModelProvider(this, viewModelFactory).get(ListTrackViewModel::class.java)

        val playbackServiceConnector = PlaybackServiceConnector
            .getInstance(requireContext(), ComponentName(requireContext(), PlaybackService::class.java))
        val nowPlayingViewModelFactory = NowPlayingViewModelFactory(playbackServiceConnector)
        val nowPlayingViewModel = ViewModelProvider(this, nowPlayingViewModelFactory).get(
            NowPlayingViewModel::class.java)

        binding.listTrackViewModel = listTrackViewModel
        binding.lifecycleOwner = this


        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.trackVList.layoutManager = manager

        val adapter = ListTrackAdapter(TrackListener { id ->
            listTrackViewModel.onTrackClicked(id)
            nowPlayingViewModel.sendQueueToConnector(listTrackViewModel.tracks.value)
            nowPlayingViewModel.playTrack(id)
        })

        binding.trackVList.adapter = adapter

        listTrackViewModel.navigateToNowPlayingScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                navController.navigate(AlbumScreenDirections.actionAlbumScreenToNowPlaying(id))
                listTrackViewModel.doneNavigating()
            }
        })

        listTrackViewModel.tracks.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}