package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListTrackViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListTrackViewModelFactory::class.java)) {
            return ListTrackViewModelFactory(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}