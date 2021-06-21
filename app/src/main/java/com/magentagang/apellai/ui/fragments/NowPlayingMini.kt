package com.magentagang.apellai.ui.fragments

import android.content.ComponentName
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.MobileNavigationDirections
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentNowPlayingMiniBinding
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModel
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModelFactory
import com.magentagang.apellai.util.RepositoryUtils
import com.magentagang.apellai.util.getNightModeEnabled

class NowPlayingMini : Fragment() {

    private lateinit var playbackServiceConnector: PlaybackServiceConnector
    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var palette: Palette

    lateinit var binding: FragmentNowPlayingMiniBinding
    private lateinit var imageView: ImageView
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.placeholder_nocover)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNowPlayingMiniBinding.inflate(inflater, container, false)
        imageView = binding.nowPlayingMiniArt
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

        nowPlayingViewModel.trackInfo.observe(viewLifecycleOwner, { track ->
            updateUI(track)
        })
        nowPlayingViewModel.playPauseButtonResMini.observe(viewLifecycleOwner, { res ->
            binding.nowPlayingMiniPlayPauseButton.setImageResource(res)
        })
        nowPlayingViewModel.trackPos.observe(viewLifecycleOwner, { currentPos ->
            binding.nowPlayingMiniProgressBar.progress = currentPos.div(1000).toInt()
        })
        nowPlayingViewModel.trackBufferPos.observe(viewLifecycleOwner, {
                currentBufferPos ->
            binding.nowPlayingMiniProgressBar.secondaryProgress = currentBufferPos.div(1000).toInt()
        })

        binding.nowPlayingMiniDetails.setOnClickListener {
            nowPlayingViewModel.trackInfo.value?.let {
                nowPlayingViewModel.onNowPlayingMiniClicked(it.id)
            }
        }

        nowPlayingViewModel.navigateToNowPlayingScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                navController.navigate(MobileNavigationDirections.actionNowPlayingMiniToNowPlaying(id))
                nowPlayingViewModel.doneNavigating()
            }
        })

        // TODO Trigger play/pause event on button press in the XML instead
        binding.nowPlayingMiniPlayPauseButton.setOnClickListener {
            nowPlayingViewModel.trackInfo.value?.let {
                nowPlayingViewModel.playTrack(it.id)
            }
        }
    }

    private fun updateUI(track: Track) = with(binding) {
        nowPlayingMiniTrackName.text = track.title
        nowPlayingMiniTrackArtist.text = track.artist
        nowPlayingMiniProgressBar.max = track.duration
        loadImage(track)
        updatePalette()
    }

    private fun loadImage(track: Track) {
        Glide.with(this)
            .applyDefaultRequestOptions(glideOptions)
            .load(RepositoryUtils.getCoverArtUrl(track.coverArt!!))
            .placeholder(R.drawable.placeholder_nocover)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    private fun updatePalette() {
        val image = imageView.drawable.toBitmap()
        val defaultColor = resources.getColor(R.color.primary_text, context?.theme)
        palette = Palette.from(image).generate()
        val lightColor = palette.getLightMutedColor(defaultColor)
        val darkColor = palette.getDarkMutedColor(defaultColor)
        val colorToApply = when(getNightModeEnabled(requireContext())) {
            Configuration.UI_MODE_NIGHT_YES -> lightColor
            else -> darkColor
        }

        binding.nowPlayingMiniTrackName.setTextColor(colorToApply)
        binding.nowPlayingMiniTrackArtist.setTextColor(colorToApply)
        binding.nowPlayingMiniProgressBar.progressTintList = ColorStateList.valueOf(colorToApply)
        binding.nowPlayingMiniPlayPauseButton.setColorFilter(colorToApply)
    }

}