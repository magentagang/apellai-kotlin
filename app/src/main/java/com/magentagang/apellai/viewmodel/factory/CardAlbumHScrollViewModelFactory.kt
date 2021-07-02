package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.viewmodel.CardAlbumHScrollViewModel

class CardAlbumHScrollViewModelFactory(
    private val application: Application,
    private val AlbumType: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardAlbumHScrollViewModel::class.java)) {
            return CardAlbumHScrollViewModel(application, AlbumType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}