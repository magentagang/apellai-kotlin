package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.R
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


class CardAlbumHScrollViewModel(application: Application, albumType: String) :
    AndroidViewModel(application) {
    var albums: LiveData<List<Album>>
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)
    var viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        albums = when (albumType) {
            application.resources.getString(R.string.loved_albums) -> databaseDao.getStarredAlbums()
                .asLiveData()
            application.resources.getString(R.string.most_played) -> databaseDao.getFrequentAlbums()
                .asLiveData()
            application.resources.getString(R.string.recently_added) -> databaseDao.getNewestAlbums()
                .asLiveData()
            application.resources.getString(R.string.random) -> databaseDao.getRandomAlbums()
                .asLiveData()
            application.resources.getString(R.string.recently_played) -> databaseDao.getRecentAlbums()
                .asLiveData()
            else -> databaseDao.getStarredAlbums().asLiveData()
        }
    }

    private val _navigateToAlbumScreen: MutableLiveData<String?> = MutableLiveData<String?>()
    val navigateToAlbumScreen
        get() = _navigateToAlbumScreen

    fun onAlbumClicked(id: String) {
        _navigateToAlbumScreen.value = id
    }

    fun doneNavigating() {
        _navigateToAlbumScreen.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}