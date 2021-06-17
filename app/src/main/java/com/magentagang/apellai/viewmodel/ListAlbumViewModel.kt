package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Constants
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase

class ListAlbumViewModel(application: Application) : AndroidViewModel(application) {
    var repositoryUtils: RepositoryUtils
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()

    val albums : LiveData<List<Album>>
    private val dataSource = UserDatabase.getInstance(application).databaseDao()

    init{
        repositoryUtils = RepositoryUtils(databaseDao)
        repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
        dataSource.getAllAlbums().asLiveData().also { albums = it }
    }

    private val _navigateToAlbumScreen: MutableLiveData<String?> = MutableLiveData<String?>()
    val navigateToAlbumScreen
        get() = _navigateToAlbumScreen

    fun onAlbumClicked(id: String){
        _navigateToAlbumScreen.value = id
    }
    fun doneNavigating() {
        _navigateToAlbumScreen.value = null
    }

}