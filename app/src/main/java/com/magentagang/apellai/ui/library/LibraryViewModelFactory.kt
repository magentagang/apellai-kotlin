package com.magentagang.apellai.ui.library

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.ui.home.HomeViewModel

class LibraryViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}