package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils

class ListArtistViewModel ( application: Application) : AndroidViewModel(application) {
    var repositoryUtils: RepositoryUtils
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    private val dataSource = UserDatabase.getInstance(application).databaseDao()
    val artists : LiveData<List<Artist>>
    init{
        repositoryUtils = RepositoryUtils(databaseDao)
        repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
        dataSource.getAllArtists().asLiveData().also { artists = it }
    }


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