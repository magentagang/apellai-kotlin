package com.magentagang.apellai.repository.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.findIndex
import com.magentagang.apellai.util.toMediaMetadataCompat
import kotlinx.coroutines.*
import timber.log.Timber

class PlaybackService : MediaBrowserServiceCompat() {

    private lateinit var notificationManager: NotificationManager
    private val mediaSource = MediaSource.getInstance()
    private var isForegroundService = false

    private lateinit var player: Player

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    lateinit var mediaPlaybackStateBuilder: PlaybackStateCompat.Builder

    private var currentMediaQueue: List<MediaMetadataCompat> = emptyList()

    private val apellaiAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val playerListener = PlayerListener()

    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(apellaiAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, 0)
            }

        mediaSession = MediaSessionCompat(this, "ApellaiService").apply {
            setSessionActivity(sessionActivityPendingIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        notificationManager = NotificationManager(
            this,
            mediaSession.sessionToken,
            PlayerNotificationListener()
        )

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(PlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(TimelineQueueNavigator(mediaSession))

        player = exoPlayer
        mediaSessionConnector.setPlayer(player)

        notificationManager.showNotificationForPlayer(player)

        // TODO Storage

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // TODO Save the song to cache
        super.onTaskRemoved(rootIntent)
        player.stop()
        player.clearMediaItems()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot("__AWOOGA__", null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        serviceJob.cancel()

        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    private fun enqueueToPlayer(
        fileList: List<MediaMetadataCompat>,
        trackToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean
    ) {

        val initialIndex = if (trackToPlay == null) 0 else fileList.findIndex(trackToPlay)
        Timber.v("initialIndex is $initialIndex")
        currentMediaQueue = fileList

        player.playWhenReady = playWhenReady
        player.stop()
        player.clearMediaItems()

        val factory = DefaultDataSourceFactory(
            this,
            Constants.USER_AGENT,
            null
        )

        val concatenatingMediaSource = ConcatenatingMediaSource()
        for (file in fileList) {
            concatenatingMediaSource.addMediaSource(
                ProgressiveMediaSource.Factory(factory).createMediaSource(
                    MediaItem.fromUri(file.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
                )
            )
        }

        exoPlayer.prepare()
        exoPlayer.setMediaSource(concatenatingMediaSource)
        exoPlayer.seekTo(initialIndex, 0L)

    }

    private fun cacheCurrentSong() {
        // TODO Cache the last played song so that it can be retrieved on kill
    }

    private fun buildQueue(mediaMetadataCompat: MediaMetadataCompat): List<MediaMetadataCompat> {
        val loadedQueue = mediaSource.loadTracks()
        return if (loadedQueue.isEmpty()) {
            listOf(mediaMetadataCompat)
        } else {
            loadedQueue.toMediaMetadataCompat()
        }
    }

    inner class PlayerListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING, Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(player)
                    if (playbackState == Player.STATE_READY) {
                        // TODO Save song to SharedPrefs/DB
                        if (!playWhenReady) {
                            stopForeground(false)
                            isForegroundService = false
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            // TODO Show error as toast
        }
    }

    inner class PlaybackPreparer : MediaSessionConnector.PlaybackPreparer {

        override fun getSupportedPrepareActions(): Long {
            return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
        }

        override fun onPrepare(playWhenReady: Boolean) {}

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Timber.v("onPrepareFromMediaId mediaId: $mediaId")
            var trackToPlay: MediaMetadataCompat?

            val viewModelJob = Job()
            val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

            coroutineScope.launch {
                val testTrackDeferred = SubsonicApi.retrofitService.getTrackAsync(id = mediaId)
                try {
                    val testTrackRoot = testTrackDeferred.await()
                    val testTrack = testTrackRoot.subsonicResponse.track

                    Timber.v("Fetched track: $testTrack")

                    trackToPlay = testTrack?.toMediaMetadataCompat()

                    Timber.v("Converted Track to MMC: $trackToPlay")

                    // TODO check queue and send here
                    trackToPlay?.let {
                        Timber.v("Enqueued mediaId $mediaId to player")
                        enqueueToPlayer(
                            buildQueue(trackToPlay!!),
                            trackToPlay,
                            playWhenReady
                        )
                        Timber.v("Player State: ${player.playbackState}")
                    }
                } catch (e: Exception) {
                    Timber.e(e.message.toString())
                    Timber.e("Failed to fetch track $mediaId")
                }
            }

        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {}

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {}

        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ) = false
    }

    inner class TimelineQueueNavigator(mediaSession: MediaSessionCompat) :
        com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return currentMediaQueue[windowIndex].description
        }
    }

    inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, this@PlaybackService.javaClass)
            )

            startForeground(notificationId, notification)
            isForegroundService = true
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            // FIXME Find a solution to service not restarting on app kill on alternating launches
//            stopSelf()
        }
    }
}