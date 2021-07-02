package com.magentagang.apellai.repository.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.magentagang.apellai.R
import com.magentagang.apellai.util.Constants
import kotlinx.coroutines.*

class NotificationManager(
    val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    init {

        val mediaController = MediaControllerCompat(context, sessionToken)

//        notificationManager = PlayerNotificationManager.Builder(
//            context,
//            Constants.NOW_PLAYING_NOTIFICATION_ID,
//            Constants.NOW_PLAYING_CHANNEL_ID,
//            MediaDescriptionAdapter(mediaController)
//        ).setNotificationListener(notificationListener)
//            .build()
//            .apply {
//                setMediaSessionToken(sessionToken)
//                setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                setControlDispatcher(DefaultControlDispatcher(0, 0))
//            }

        // TODO Avoid using deprecated method if possible
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            Constants.NOW_PLAYING_CHANNEL_ID,
            R.string.now_playing,
            R.string.now_playing_desc,
            MediaDescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.music_line)
            setControlDispatcher(DefaultControlDispatcher(0, 0))
        }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    inner class MediaDescriptionAdapter(private val controllerCompat: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        private var iconUri: Uri? = null
        private var bitmap: Bitmap? = null

        override fun getCurrentContentTitle(player: Player) =
            controllerCompat.metadata.description.title.toString()

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controllerCompat.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controllerCompat.metadata.description.subtitle.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return if (iconUri != controllerCompat.metadata.description.iconUri || bitmap == null) {
                iconUri = controllerCompat.metadata.description.iconUri
                serviceScope.launch {
                    bitmap = iconUri?.let {
                        getCoverFromUri(it)
                    }
                    bitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                bitmap
            }
        }

        private suspend fun getCoverFromUri(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                @Suppress("BlockingMethodInNonBlockingContext")
                Glide.with(context).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit(
                        Constants.NOTIFICATION_LARGE_ICON_SIZE,
                        Constants.NOTIFICATION_LARGE_ICON_SIZE
                    )
                    .get()
            }
        }
    }

}

private val glideOptions = RequestOptions()
    .fallback(R.drawable.placeholder_nocover)
    .diskCacheStrategy(DiskCacheStrategy.DATA)