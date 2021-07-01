package com.magentagang.apellai.ui.nowplayingscreen

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.lifecycle.*
import com.magentagang.apellai.R
import com.magentagang.apellai.model.ErrorHandler
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.service.BLANK_TRACK
import com.magentagang.apellai.repository.service.EMPTY_PLAYBACK_STATE
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.repository.service.SubsonicApi
import com.magentagang.apellai.util.*
import kotlinx.coroutines.*
import timber.log.Timber

class NowPlayingViewModel(playbackServiceConnector: PlaybackServiceConnector, application: Application) : AndroidViewModel(application){

    val _loveButtonLivedata = MutableLiveData<Boolean?>(null)

    val loveButtonLivedata: LiveData<Boolean?>
     get() = _loveButtonLivedata

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val coroutineScopeIO = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun getLovedStatusAsync(track: Track) {
        CoroutineScope(Dispatchers.IO).launch {
            val trackListDeferred = SubsonicApi.retrofitService.getStarred2Async()
            try {
                val root = trackListDeferred.await()
                var isLovedAlready = false
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.starred2?.song != null)
                {
                    val songList = root.subsonicResponse.starred2.song
                    Timber.i("LoveButton -> ${songList?:"LoveButton-> songList is null"}")
                    if(songList != null){

                        for(starredSongs in songList)
                        {
                            if(starredSongs.id==track.id) {
                                Timber.i("LoveButton -> $starredSongs")
                                _loveButtonLivedata.postValue(true)
                                isLovedAlready = true
                            }
                        }
                    }
                }else if(root.subsonicResponse.status == "failed"){
                    //TODO(Log error messages using ErrorHandler Interface here)
                    Timber.i(root.subsonicResponse.error?.message?:"ERROR in getLovedStatusAsync()")
                }
                if(!isLovedAlready){
                    _loveButtonLivedata.postValue(false)
                }
                Timber.i("LoveButton -> getLovedStatusAsync -> ${_loveButtonLivedata.value}")
            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun setLoveButtonLiveDataToNull(){
        _loveButtonLivedata.postValue(null)
    }

    fun unstarTrack(b: Boolean, _id: String) {
        coroutineScopeIO.launch {
            if(b){
                Timber.i("LoveButton -> Unstarring track")
                val unstarTrackDeferred = SubsonicApi.retrofitService.unstarSongAsync(id = _id)
                try{
                    val root = unstarTrackDeferred.await()
                    if(root.subsonicResponse.status != "failed"){
                        Timber.i("LoveButton -> Unstarring track successful")
                        _loveButtonLivedata.postValue(false)
                    }else{
                        Timber.i("LoveButton -> Unstarring track unsuccessful")
                        Timber.i("Error -> nowPlayingViewModel -> unstarTrack -> ${ErrorHandler.logErrorMessage(subsonicResponseError = root.subsonicResponse.error)}")
                    }
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }else{
                Timber.i("LoveButton -> Starring track")
                val starTrackDeferred = SubsonicApi.retrofitService.starSongAsync(id = _id)
                try{
                    val root = starTrackDeferred.await()
                    if(root.subsonicResponse.status != "failed"){
                        Timber.i("LoveButton -> Starring track successful")
                        _loveButtonLivedata.postValue(true)
                    }else{
                        Timber.i("LoveButton -> Starring track unsuccessful")
                        Timber.i("Error -> nowPlayingViewModel -> unstarTrack -> ${ErrorHandler.logErrorMessage(subsonicResponseError = root.subsonicResponse.error)}")
                    }
                }catch(e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }


    // FIXME Shuffle and repeat icon reset bug
    val _shuffleMode = MutableLiveData(Constants.SHUFFLE_MODE)
    val shuffleMode : LiveData<Int>
        get() = _shuffleMode
//    var repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE
    val _repeatMode = MutableLiveData(Constants.REPEAT_MODE)
    val repeatMode: LiveData<Int>
        get() = _repeatMode

    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    val trackInfo = MutableLiveData<Track>()
    val trackPos = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    val trackBufferPos = MutableLiveData<Long>().apply {
        postValue(0L)
    }

    val playPauseButtonRes = MutableLiveData<Int>().apply {
        postValue(R.drawable.play_circle_line)
    }

    val playPauseButtonResMini = MutableLiveData<Int>().apply {
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

    private val playbackServiceConnector = playbackServiceConnector.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.currentlyPlayingFile.observeForever(mediaMetadataObserver)
        updateTrackPos()
    }

    private val _navigateToNowPlayingScreen = MutableLiveData<String?>()
    val navigateToNowPlayingScreen
        get() = _navigateToNowPlayingScreen

    fun onNowPlayingMiniClicked(id: String){
        _navigateToNowPlayingScreen.value = id
    }

    fun doneNavigating() {
        _navigateToNowPlayingScreen.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playbackServiceConnector.playbackState.removeObserver(playbackStateObserver)
        playbackServiceConnector.currentlyPlayingFile.removeObserver(mediaMetadataObserver)
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
                val testTrackDeferred =
                    SubsonicApi.retrofitService.getTrackAsync(id = mediaMetadataCompat.id!!)
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
            when (playbackStateCompat.isPlaying) {
                true -> R.drawable.pause_circle_line
                else -> R.drawable.play_circle_line
            }
        )

        playPauseButtonResMini.postValue(
            when (playbackStateCompat.isPlaying) {
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

    fun nextTrack() {
        // TODO Stop playback if there are no tracks after
        playbackServiceConnector.transportControls.skipToNext()
    }

    fun prevTrack() {
        playbackServiceConnector.transportControls.skipToPrevious()
    }

    fun toggleShuffle() {
        Timber.i("Shuffle -> ${_shuffleMode.value}")
        when(_shuffleMode.value) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                Constants.SHUFFLE_MODE = PlaybackStateCompat.SHUFFLE_MODE_ALL
            }
            else -> {
                Constants.SHUFFLE_MODE = PlaybackStateCompat.SHUFFLE_MODE_NONE
            }
        }
        _shuffleMode.postValue(Constants.SHUFFLE_MODE)
        playbackServiceConnector.transportControls.setShuffleMode(_shuffleMode.value!!)
    }

    fun toggleRepeat() {
        Constants.REPEAT_MODE = when(_repeatMode.value) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> PlaybackStateCompat.REPEAT_MODE_ALL
            PlaybackStateCompat.REPEAT_MODE_ALL -> PlaybackStateCompat.REPEAT_MODE_ONE
            else -> PlaybackStateCompat.REPEAT_MODE_NONE
        }
        _repeatMode.postValue(Constants.REPEAT_MODE)
        playbackServiceConnector.transportControls.setRepeatMode(Constants.REPEAT_MODE)
    }

    inner class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                playbackServiceConnector.transportControls.seekTo(progress.toLong().times(1000L))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

}