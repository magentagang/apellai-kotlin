package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.model.Constants
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase

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