package com.magentagang.apellai.util

import android.content.Context
import android.content.res.Configuration
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.util.RepositoryUtils.Companion.getCoverArtUrl
import com.magentagang.apellai.util.RepositoryUtils.Companion.getStreamUri

fun Track.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, track.toLong())
        .putString(MediaMetadataCompat.METADATA_KEY_DATE, year.toString())
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong() * 1000L)
        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
        .putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, discNumber?.toLong() ?: 0L)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, getStreamUri().toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, getCoverArtUrl(id))
        .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, getCoverArtUrl(id))
        .build()
}

fun List<Track>.toMediaMetadataCompat(): List<MediaMetadataCompat> {
    val mediaList = arrayListOf<MediaMetadataCompat>()
    for (track in this) {
        mediaList.add(track.toMediaMetadataCompat())
    }
    return mediaList
}

fun List<MediaMetadataCompat>.findIndex(compareTo: MediaMetadataCompat?): Int {
    compareTo?.let {
        for (index in this.indices) {
            if (this[index].id == compareTo.id) {
                return index
            }
        }
    }
    return -1
}



inline val MediaMetadataCompat.id: String?
    get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.duration
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline val PlaybackStateCompat.isPlaying
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING)

inline val PlaybackStateCompat.isPlayEnabled
    get() = (actions and PlaybackStateCompat.ACTION_PLAY != 0L) ||
            ((actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) &&
                    (state == PlaybackStateCompat.STATE_PAUSED))

inline val PlaybackStateCompat.isPrepared
    get() = (state == PlaybackStateCompat.STATE_BUFFERING) ||
            (state == PlaybackStateCompat.STATE_PLAYING) ||
            (state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.currentPlayBackPosition: Long
    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else {
        position
    }

inline val PlaybackStateCompat.currentBufferPosition: Long
    get() = bufferedPosition

fun Int.toMSS(): String {
    return this.div(60).toString() + ":" + (this.rem(60)).toString().padStart(2, '0')
}

fun getNightModeEnabled(context: Context): Int {
    return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
}

// Extremely hacky implementation since setCustomAnimations doesn't seem to work on fragment hide
fun Fragment.hideWithAnimation(
    context: Context,
    fragmentManager: FragmentManager,
    animationId: Int)
{
    val fragment = this
    val exitAnimation = AnimationUtils.loadAnimation(context, animationId) .apply {
        duration = 500
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                fragmentManager.beginTransaction()
                    .hide(fragment)
                    .commit()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
    }
    fragment.view?.startAnimation(exitAnimation)
}