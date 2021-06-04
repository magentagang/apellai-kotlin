package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist

class ListArtistViewModel ( application: Application) : AndroidViewModel(application) {
    private var artist = MutableLiveData<Artist?>()
    private val _artists = MutableLiveData<List<Artist>>().apply {
        postValue(listOf(
            Artist("1"),
            Artist("2"),
            Artist("3"),
            Artist("4"),
            Artist("5")
        ))
    }
    val artists : LiveData<List<Artist>> = _artists

    private val _navigateToArtistScreen = MutableLiveData<String?>()
    val navigateToArtistScreen
        get() = _navigateToArtistScreen

    fun onArtistClicked(id: String){
        _navigateToArtistScreen.value = id
    }
    fun doneNavigating() {
        _navigateToArtistScreen.value = null
    }
}