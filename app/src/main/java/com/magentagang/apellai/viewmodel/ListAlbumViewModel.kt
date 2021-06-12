package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.UserDatabase

class ListAlbumViewModel(application: Application) : AndroidViewModel(application) {

    val albums : LiveData<List<Album>>
    val dataSource = UserDatabase.getInstance(application).databaseDao()
    init{
        albums = dataSource.getAllAlbums().asLiveData()
    }

    private val _navigateToAlbumScreen = MutableLiveData<String?>()
    val navigateToAlbumScreen
        get() = _navigateToAlbumScreen

    fun onAlbumClicked(id: String){
        _navigateToAlbumScreen.value = id
    }
    fun doneNavigating() {
        _navigateToAlbumScreen.value = null
    }

}