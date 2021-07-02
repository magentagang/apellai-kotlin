package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.viewmodel.ListArtistViewModel

class ListArtistViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListArtistViewModel::class.java)) {
            return ListArtistViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}