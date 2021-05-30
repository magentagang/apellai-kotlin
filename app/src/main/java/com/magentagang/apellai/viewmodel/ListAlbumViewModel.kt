package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.model.Album

class ListAlbumViewModel(application: Application) : AndroidViewModel(application) {
    private var album = MutableLiveData<Album?>()
    private val _albums = MutableLiveData<List<Album>>().apply {
        postValue(listOf(
            Album("1"),
            Album("2"),
            Album("3"),
            Album("4"),
            Album("5"),
            Album("6"),
            Album("7"),
            Album("8"),
            Album("9"),
        ))
    }
    val albums : LiveData<List<Album>> = _albums
}