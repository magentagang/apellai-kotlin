package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.R
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import kotlinx.coroutines.*
import timber.log.Timber


class CardAlbumHScrollViewModel(application: Application, albumType : String) : AndroidViewModel(application) {
    var albums: LiveData<List<Album>>
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils:RepositoryUtils = RepositoryUtils(databaseDao)
    var viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init{
        albums = when(albumType)
        {
            application.resources.getString(R.string.loved_albums) -> databaseDao.getStarredAlbums().asLiveData()
            application.resources.getString(R.string.most_played) -> databaseDao.getFrequentAlbums().asLiveData()
            application.resources.getString(R.string.recently_added) -> databaseDao.getNewestAlbums().asLiveData()
            application.resources.getString(R.string.random) -> databaseDao.getRandomAlbums().asLiveData()
            application.resources.getString(R.string.recently_played) -> databaseDao.getRecentAlbums().asLiveData()
            else -> databaseDao.getStarredAlbums().asLiveData()
        }
        // TODO(MAKE IT SO THAT IT ONLY GETS CALLED WHEN FRAGMENT WAS CREATED THE FIRST TIME)
        Timber.i("INIT CALLED")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}