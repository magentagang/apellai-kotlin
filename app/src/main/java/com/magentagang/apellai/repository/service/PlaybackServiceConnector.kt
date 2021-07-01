package com.magentagang.apellai.repository.service

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.id
import timber.log.Timber

class PlaybackServiceConnector(context: Context, serviceComponent: ComponentName) {
    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val networkFailure = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }
    val currentlyPlayingFile = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(BLANK_TRACK) }

    private lateinit var mediaControllerCompat: MediaControllerCompat
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaControllerCompat.transportControls

    private val mediaBrowserCompatConnectionCallback = MediaBrowserCompatConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserCompatConnectionCallback,
        null
    ).apply { connect() }

    private val mediaControllerCompatCallback = MediaControllerCompatCallback()

    private inner class MediaBrowserCompatConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Timber.v("${object{}.javaClass.enclosingMethod?.name.toString()} executed")
            mediaControllerCompat = MediaControllerCompat(context, mediaBrowser.sessionToken)
                .apply { registerCallback(mediaControllerCompatCallback) }
            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCompatCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Timber.v("${object{}.javaClass.enclosingMethod?.name.toString()} executed")
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Timber.v("${object{}.javaClass.enclosingMethod?.name.toString()} executed")
            currentlyPlayingFile.postValue(
                if (metadata?.id == null) {
                    BLANK_TRACK
                } else {
                    metadata
                }
            )
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {}

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                Constants.NETWORK_FAILURE -> networkFailure.postValue(true)
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserCompatConnectionCallback.onConnectionSuspended()
        }

    }

    companion object {
        @Volatile
        private var instance: PlaybackServiceConnector? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: PlaybackServiceConnector(context, serviceComponent)
                    .also { instance = it }
            }
    }

}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0F)
    .build()

val BLANK_TRACK: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0L)
    .build()