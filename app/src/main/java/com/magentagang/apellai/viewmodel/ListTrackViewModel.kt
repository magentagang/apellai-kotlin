package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Track

class ListTrackViewModel(application : Application): AndroidViewModel(application)  {
//TODO similar to ListAlbumViewModel
private var track = MutableLiveData<Track?>()
    private val _tracks = MutableLiveData<List<Track>>().apply {
        postValue(listOf(
            Track("1"),
            Track("2"),
            Track("3"),
            Track("4"),
            Track("5"),
            Track("6"),
            Track("7"),
            Track("8"),
            Track("9"),
        ))
    }
    val tracks : LiveData<List<Track>> = _tracks
}