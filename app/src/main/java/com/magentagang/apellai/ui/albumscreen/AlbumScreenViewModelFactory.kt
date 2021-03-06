package com.magentagang.apellai.ui.albumscreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AlbumScreenViewModelFactory(private val application: Application, private val id: String) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumScreenViewModel::class.java)) {
            return AlbumScreenViewModel(application, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}