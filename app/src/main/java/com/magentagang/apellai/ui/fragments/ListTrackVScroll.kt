package com.magentagang.apellai.ui.fragments

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.MainActivity
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.ListTrackAdapter
import com.magentagang.apellai.adapter.TrackListener
import com.magentagang.apellai.databinding.FragmentListTrackVScrollBinding
import com.magentagang.apellai.repository.service.MediaSource
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModel
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModelFactory
import com.magentagang.apellai.viewmodel.ListTrackViewModel
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
        val albumId = container?.tag.toString()
        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListTrackViewModelFactory(application, albumId)
        val listTrackViewModel =
            ViewModelProvider(this, viewModelFactory).get(ListTrackViewModel::class.java)

        val playbackServiceConnector = PlaybackServiceConnector
            .getInstance(
                requireContext(),
                ComponentName(requireContext(), PlaybackService::class.java)
            )
        val nowPlayingViewModelFactory =
            NowPlayingViewModelFactory(playbackServiceConnector, application)
        val nowPlayingViewModel = ViewModelProvider(this, nowPlayingViewModelFactory).get(
            NowPlayingViewModel::class.java
        )
        val mediaSource = MediaSource.getInstance()

        binding.listTrackViewModel = listTrackViewModel
        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.trackVList.layoutManager = manager

        val adapter = ListTrackAdapter(TrackListener { id ->
            listTrackViewModel.tracks.value?.let {
                mediaSource.storeTracks(listTrackViewModel.tracks.value!!)
            }
            nowPlayingViewModel.playTrack(id)
            MainActivity.showNowPlayingMini.postValue(true)
        })

        binding.trackVList.adapter = adapter

        listTrackViewModel.tracks.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }


}