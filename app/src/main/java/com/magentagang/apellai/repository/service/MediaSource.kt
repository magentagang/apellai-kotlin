package com.magentagang.apellai.repository.service

import com.magentagang.apellai.model.Track

class MediaSource {

    private var queue: List<Track> = emptyList()

    fun storeTracks(tracks: List<Track>) {
        queue = tracks
    }

    fun loadTracks(): List<Track> = queue

    companion object {
        @Volatile
        private var instance: MediaSource? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: MediaSource()
                    .also { instance = it }
            }
    }
}