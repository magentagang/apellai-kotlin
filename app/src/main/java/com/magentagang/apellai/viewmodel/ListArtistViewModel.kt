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
    val dataSource = UserDatabase.getInstance(application).databaseDao()
//    private var artist = MutableLiveData<Artist?>()
//    private val _artists = MutableLiveData<List<Artist>>().apply {
//        postValue(listOf(
//            Artist("1"),
//            Artist("2"),
//            Artist("3"),
//            Artist("4"),
//            Artist("5")
//        ))
//    }
//    val artists : LiveData<List<Artist>> = _artists
    // TODO(SHOW THIS LIVE DATA ON ARTISTS LIST)
    val artists : LiveData<List<Artist>>
    init{
        repositoryUtils = RepositoryUtils(databaseDao)
        repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
        artists = databaseDao.getAllArtists().asLiveData()
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