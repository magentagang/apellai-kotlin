package com.magentagang.apellai.ui.albumscreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.ui.search.SearchViewModel
import com.magentagang.apellai.viewmodel.CardAlbumHScrollViewModel


class AlbumScreenViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumScreenViewModel::class.java)) {
            return AlbumScreenViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}