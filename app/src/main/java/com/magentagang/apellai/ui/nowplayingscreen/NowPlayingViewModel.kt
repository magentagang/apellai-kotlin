package com.magentagang.apellai.ui.nowplayingscreen

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.magentagang.apellai.R
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.service.BLANK_TRACK
import com.magentagang.apellai.repository.service.EMPTY_PLAYBACK_STATE
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.repository.service.SubsonicApi
import com.magentagang.apellai.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class NowPlayingViewModel(playbackServiceConnector: PlaybackServiceConnector) : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    val trackInfo = MutableLiveData<Track>()
    val trackPos = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    val trackBufferPos = MutableLiveData<Long>().apply {
        postValue(0L)
    }

    val playPauseButtonRes = MutableLiveData<Int>().apply {
        postValue(R.drawable.play_fill)
    }

    val seekBarChangeListener = OnSeekBarChangeListener()

    private var updatePos = true
    // TODO Check if replaceable with Coroutines
    private val handler = Handler(Looper.getMainLooper())

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        Timber.v("playbackStateObserver")
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = playbackServiceConnector.currentlyPlayingFile.value ?: BLANK_TRACK
        updateState(playbackState, metadata)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        Timber.v("mediaMetadataObserver")
        updateState(playbackState, it)
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {}
    }

    private val playbackServiceConnector = playbackServiceConnector.also {
//        it.subscribe(trackInfo.value?.id ?: "awooga", subscriptionCallback)
        it.playbackState.observeForever(playbackStateObserver)
        it.currentlyPlayingFile.observeForever(mediaMetadataObserver)
        updateTrackPos()
    }

    override fun onCleared() {
        super.onCleared()
        playbackServiceConnector.playbackState.removeObserver(playbackStateObserver)
        playbackServiceConnector.currentlyPlayingFile.removeObserver(mediaMetadataObserver)
//        playbackServiceConnector.unsubscribe(trackInfo.value?.id ?: "awooga", subscriptionCallback)
        updatePos = false
    }

    private fun updateTrackPos(): Boolean = handler.postDelayed({
        val currentPos = playbackState.currentPlayBackPosition
        val currentBufferPos = playbackState.currentBufferPosition
        if (trackPos.value != currentPos)
            trackPos.postValue(currentPos)
        if (trackBufferPos.value != currentBufferPos)
            trackBufferPos.postValue(currentBufferPos)
        if (updatePos)
            updateTrackPos()
    }, Constants.SEEKBAR_UPDATE_INTERVAL)

    private fun updateState(
        playbackStateCompat: PlaybackStateCompat,
        mediaMetadataCompat: MediaMetadataCompat
    ) {
        if (mediaMetadataCompat.duration != 0L && mediaMetadataCompat.id != null) {
            coroutineScope.launch {
                val testTrackDeferred = SubsonicApi.retrofitService.getTrackAsync(id = mediaMetadataCompat.id!!)
                try {
                    val testTrackRoot = testTrackDeferred.await()
                    val testTrack = testTrackRoot.subsonicResponse.track
                    Timber.v("Fetched track in VM: $testTrack")
                    trackInfo.postValue(testTrack!!)
                } catch (e: Exception) {
                    Timber.e(e.message.toString())
                }
            }
        }

        playPauseButtonRes.postValue(
            when(playbackStateCompat.isPlaying) {
                true -> R.drawable.pause_fill
                else -> R.drawable.play_fill
            }
        )
    }

    fun playTrack(trackId: String) {
        val currentTrack = playbackServiceConnector.currentlyPlayingFile.value
        val transportControls = playbackServiceConnector.transportControls

        val isEnqueued = playbackServiceConnector.playbackState.value?.isPrepared ?: false
        if (isEnqueued && trackId == currentTrack?.id) {
            playbackServiceConnector.playbackState.value?.let { playbackStateCompat ->
                when {
                    playbackStateCompat.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> Timber.w("AAAAAAAAAAAAAAAAAAA")
                }
            }
        } else {
            transportControls.playFromMediaId(trackId, null)
        }
    }

    // TODO Extremely finicky, file needs to be cached or sth
    inner class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            playbackServiceConnector.transportControls.seekTo(progress.toLong().times(1000L))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

}