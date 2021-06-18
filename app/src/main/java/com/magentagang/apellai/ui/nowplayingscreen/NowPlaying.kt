package com.magentagang.apellai.ui.nowplayingscreen

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.databinding.FragmentNowPlayingBinding
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.util.toMSS

class NowPlaying : Fragment() {

    private lateinit var playbackServiceConnector: PlaybackServiceConnector
    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    lateinit var binding: FragmentNowPlayingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity ?: return

        playbackServiceConnector = PlaybackServiceConnector
            .getInstance(context, ComponentName(context, PlaybackService::class.java))
        viewModelFactory = NowPlayingViewModelFactory(playbackServiceConnector)
        nowPlayingViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NowPlayingViewModel::class.java)

        nowPlayingViewModel.trackInfo.observe(viewLifecycleOwner, {
            track -> updateUI(track)
        })
        nowPlayingViewModel.playPauseButtonRes.observe(viewLifecycleOwner, {
            res -> binding.playPauseButton.setImageResource(res)
        })
        nowPlayingViewModel.trackPos.observe(viewLifecycleOwner, {
            currentPos -> binding.startDuration.text = currentPos.toInt().div(1000).toMSS()
            binding.seekBarNowPlaying.progress = currentPos.div(1000).toInt()
        })
        nowPlayingViewModel.trackBufferPos.observe(viewLifecycleOwner, {
            // TODO Buffer not showing right info
            currentBufferPos -> binding.seekBarNowPlaying.secondaryProgress = currentBufferPos.div(1000).toInt()
        })

        binding.seekBarNowPlaying.setOnSeekBarChangeListener(nowPlayingViewModel.seekBarChangeListener)

        // TODO Trigger play/pause event on button press in the XML instead
        binding.playPauseButton.setOnClickListener {
            nowPlayingViewModel.trackInfo.value?.let {
                nowPlayingViewModel.playTrack(it.id)
            }
        }

        val initPos = 0
        binding.startDuration.text = initPos.toMSS()
        binding.endDuration.text = initPos.toMSS()
    }

    private fun updateUI(track: Track) = with(binding) {
        // TODO Update cover art
        trackNameNowPlaying.text = track.title
        trackArtistNowPlaying.text = track.artist
        endDuration.text = track.duration.toMSS()
        seekBarNowPlaying.max = track.duration
    }
}