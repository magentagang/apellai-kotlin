package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.viewmodel.GridAlbumViewModel

class GridAlbumViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GridAlbumViewModel::class.java)) {
            return GridAlbumViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}