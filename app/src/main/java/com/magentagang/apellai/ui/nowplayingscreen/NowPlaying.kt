package com.magentagang.apellai.ui.nowplayingscreen

import android.app.Application
import android.content.ComponentName
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.magentagang.apellai.MainActivity
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentNowPlayingBinding
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.util.RepositoryUtils
import com.magentagang.apellai.util.getNightModeEnabled
import com.magentagang.apellai.util.toMSS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class NowPlaying : Fragment() {

    private lateinit var playbackServiceConnector: PlaybackServiceConnector
    private lateinit var viewModelFactory: NowPlayingViewModelFactory
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var palette: Palette
    private lateinit var application: Application

    lateinit var binding: FragmentNowPlayingBinding
    private lateinit var imageView: ImageView
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.placeholder_nocover)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        application = requireNotNull(this.activity).application
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        imageView = binding.albumArtNowPlaying
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity ?: return

        playbackServiceConnector = PlaybackServiceConnector
            .getInstance(context, ComponentName(context, PlaybackService::class.java))
        viewModelFactory =
            NowPlayingViewModelFactory(playbackServiceConnector, application = application)
        nowPlayingViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NowPlayingViewModel::class.java)

        Timber.i("Shuffle -> manual check -> ${nowPlayingViewModel.shuffleMode.value}")

        nowPlayingViewModel.trackInfo.observe(viewLifecycleOwner, { track ->
            updateUI(track)
            Timber.i("LoveButton -> $track")
            CoroutineScope(Dispatchers.IO).launch {
                nowPlayingViewModel.getLovedStatusAsync(track)
            }
        })
        nowPlayingViewModel.playPauseButtonRes.observe(viewLifecycleOwner, { res ->
            binding.playPauseButton.setImageResource(res)
        })

        nowPlayingViewModel.loveButtonLivedata.observe(viewLifecycleOwner, { value ->
            value?.let {
                if (value) {
                    binding.loveButton.setImageResource(R.drawable.heart_3_fill)
                } else {
                    binding.loveButton.setImageResource(R.drawable.heart_3_line)
                }
            }
        })



        nowPlayingViewModel.shuffleMode.observe(viewLifecycleOwner, { mode ->
            Timber.i("Shuffle -> observer called $mode")
            val buttonColor = when (mode) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> resources.getColor(
                    R.color.primary_text,
                    context.theme
                )
                else -> resources.getColor(
                    R.color.disabled_toggle,
                    context.theme
                )
            }
            binding.shuffleButton.setColorFilter(buttonColor)
        })
        nowPlayingViewModel.trackPos.observe(viewLifecycleOwner, { currentPos ->
            binding.startDuration.text = currentPos.toInt().div(1000).toMSS()
            binding.seekBarNowPlaying.progress = currentPos.div(1000).toInt()
        })
        nowPlayingViewModel.trackBufferPos.observe(viewLifecycleOwner, { currentBufferPos ->
            binding.seekBarNowPlaying.secondaryProgress = currentBufferPos.div(1000).toInt()
        })

        binding.seekBarNowPlaying.setOnSeekBarChangeListener(nowPlayingViewModel.seekBarChangeListener)

        // TODO Trigger play/pause event on button press in the XML instead
        binding.playPauseButton.setOnClickListener {
            nowPlayingViewModel.trackInfo.value?.let {
                nowPlayingViewModel.playTrack(it.id)
            }
        }

        binding.prevButton.setOnClickListener {
            nowPlayingViewModel.prevTrack()
        }

        binding.nextButton.setOnClickListener {
            nowPlayingViewModel.nextTrack()
        }

        binding.shuffleButton.setOnClickListener {
            nowPlayingViewModel.toggleShuffle()
        }

        binding.loveButton.setOnClickListener {
            if (nowPlayingViewModel.loveButtonLivedata.value != null) {
                Timber.i("LoveButton -> NOT NULL")
                try {
                    if (nowPlayingViewModel.loveButtonLivedata.value!!) {
                        Timber.i("LoveButton -> NOT NULL true")
                        nowPlayingViewModel.unstarTrack(true, nowPlayingViewModel.trackInfo.value?.id ?: "")
                        binding.loveButton.setImageResource(R.drawable.heart_3_line)
                    } else {
                        Timber.i("LoveButton -> NOT NULL false")
                        nowPlayingViewModel.unstarTrack(false, nowPlayingViewModel.trackInfo.value?.id ?: "")
                        binding.loveButton.setImageResource(R.drawable.heart_3_fill)
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            } else {
                //TODO(Live data value null)
                Timber.i("LoveButton -> TAKING TOO LONG")
            }
        }

        binding.repeatButton.setOnClickListener {
            nowPlayingViewModel.toggleRepeat()
            val buttonColor = when (nowPlayingViewModel.repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_NONE -> resources.getColor(
                    R.color.disabled_toggle,
                    context.theme
                )
                else -> resources.getColor(
                    R.color.primary_text,
                    context.theme
                )
            }
            val buttonIcon = when (nowPlayingViewModel.repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ONE -> R.drawable.repeat_one_fill
                else -> R.drawable.repeat_2_fill
            }
            binding.repeatButton.setColorFilter(buttonColor)
            binding.repeatButton.setImageResource(buttonIcon)
        }

        val initPos = 0
        binding.startDuration.text = initPos.toMSS()
        binding.endDuration.text = initPos.toMSS()
    }

    override fun onStart() {
        super.onStart()
        MainActivity.showNowPlayingMini.postValue(false)
    }

    override fun onStop() {
        super.onStop()
        MainActivity.showNowPlayingMini.postValue(true)
    }

    private fun updateUI(track: Track) = with(binding) {
        trackNameNowPlaying.text = track.title
        trackArtistNowPlaying.text = track.artist
        endDuration.text = track.duration.toMSS()
        seekBarNowPlaying.max = track.duration
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
        val colorToApply = when (getNightModeEnabled(requireContext())) {
            Configuration.UI_MODE_NIGHT_YES -> lightColor
            else -> darkColor
        }

        binding.trackArtistNowPlaying.setTextColor(colorToApply)
        binding.seekBarNowPlaying.progressTintList = ColorStateList.valueOf(colorToApply)
    }



}