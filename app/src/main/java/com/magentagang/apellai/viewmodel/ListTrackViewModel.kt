package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.model.Constants
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ListTrackViewModel(application : Application, albumId : String): AndroidViewModel(application)  {
//TODO similar to ListAlbumViewModel
var repositoryUtils: RepositoryUtils
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    val albumId = albumId
    val tracks: LiveData<List<Track>>
        get() = _track
    var _track = MutableLiveData<List<Track>>()
    private val album = MutableLiveData<Album?>()
    private val dataSource = UserDatabase.getInstance(application).databaseDao()
    init{
        repositoryUtils = RepositoryUtils(databaseDao)
        repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
        coroutineScope.launch {

                val albumDeferred = repositoryUtils.fetchAlbumAsync(albumId)
                try {
                    val albumVal = albumDeferred.await()
                    if (albumVal != null) {
                        album.postValue(albumVal)
                        _track.postValue(albumVal.songList)
                    } else {
                        Timber.i("AlbumScreenViewModel-> Response albumVal value is null")
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }

        }

    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}