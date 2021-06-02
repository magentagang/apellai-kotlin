package com.magentagang.apellai.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.viewmodel.ListTrackViewModel

class ListTrackViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListTrackViewModel::class.java)) {
            return ListTrackViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}