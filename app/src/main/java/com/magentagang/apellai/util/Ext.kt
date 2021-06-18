package com.magentagang.apellai.util

import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.RepositoryUtils.Companion.getStreamUri

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
        .build()
}

fun List<Track>.toMediaMetadataCompat(): List<MediaMetadataCompat> {
    val mediaList = arrayListOf<MediaMetadataCompat>()
    for (track in this) {
        mediaList.add(track.toMediaMetadataCompat())
    }
    return mediaList
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