package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase

class CardAlbumHScrollViewModel(application: Application) : AndroidViewModel(application) {
    private var album = MutableLiveData<Album?>()
    private val _albums = MutableLiveData<List<Album>>().apply {
        postValue(listOf(
            Album("1"),
            Album("2"),
            Album("3"),
        ))
    }
    //val albums : LiveData<List<Album>> = _albums
    var albums: LiveData<List<Album>>
    lateinit var databaseDao: DatabaseDao
    init{
        databaseDao = UserDatabase.getInstance(application).databaseDao()
        albums = databaseDao.getAllAlbums().asLiveData()
    }
}