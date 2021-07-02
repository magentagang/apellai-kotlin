package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ListAlbumViewModel(application: Application, albumListType: String) :
    AndroidViewModel(application) {
    var repositoryUtils: RepositoryUtils
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    val albums: LiveData<List<Album>>
        get() = _album
    var _album = MutableLiveData<List<Album>>()
    private val artist = MutableLiveData<Artist?>()
    private val dataSource = UserDatabase.getInstance(application).databaseDao()

    init {
        Timber.i(albumListType)

        repositoryUtils = RepositoryUtils(databaseDao)
        coroutineScope.launch {
            if (albumListType != "all") {
                val artistDeferred = repositoryUtils.fetchArtistAsync(albumListType)
                try {
                    val artistVal = artistDeferred.await()
                    if (artistVal != null) {
                        artist.postValue(artistVal)
                        _album.postValue(artistVal.album)
                    } else {
                        Timber.i("AlbumScreenViewModel-> Response albumVal value is null")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        if (albumListType == "all") {
            repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
            _album = dataSource.getAllAlbums().asLiveData() as MutableLiveData
        }

        //albums = dataSource.getAllAlbums().asLiveData()
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