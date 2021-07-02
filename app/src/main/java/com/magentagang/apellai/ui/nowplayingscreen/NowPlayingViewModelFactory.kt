package com.magentagang.apellai.ui.nowplayingscreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.repository.service.PlaybackServiceConnector

class NowPlayingViewModelFactory(
    private val playbackServiceConnector: PlaybackServiceConnector,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NowPlayingViewModel::class.java)) {
            return NowPlayingViewModel(playbackServiceConnector, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}