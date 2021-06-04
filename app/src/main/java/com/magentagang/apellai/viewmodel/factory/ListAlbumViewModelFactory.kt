package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.viewmodel.ListAlbumViewModel

class ListAlbumViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListAlbumViewModel::class.java)) {
            return ListAlbumViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}