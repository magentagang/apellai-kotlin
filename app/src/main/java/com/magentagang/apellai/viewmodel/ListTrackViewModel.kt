package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ListTrackViewModel(application: Application, albumId: String) :
    AndroidViewModel(application) {

    var repositoryUtils: RepositoryUtils
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    val tracks: LiveData<List<Track>>
        get() = _track
    private var _track = MutableLiveData<List<Track>>()
    private val album = MutableLiveData<Album?>()

    init {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}