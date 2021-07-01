package com.magentagang.apellai.ui.artistscreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ArtistScreenViewModelFactory (private val application: Application, private val id : String)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistScreenViewModel::class.java)) {
            return ArtistScreenViewModel(application, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}