package com.magentagang.apellai.ui.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class SearchViewModelFactory (private val application: Application)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}