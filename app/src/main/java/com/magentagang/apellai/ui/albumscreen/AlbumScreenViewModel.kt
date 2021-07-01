package com.magentagang.apellai.ui.albumscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.ErrorHandler
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.repository.service.SubsonicApi
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AlbumScreenViewModel(application: Application, id: String) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)
    private val viewModelJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val _album = MutableLiveData<Album?>()
    val album: LiveData<Album?>
        get() = _album

    val _loveButtonLivedata = MutableLiveData<Boolean?>(null)

    val loveButtonLivedata: LiveData<Boolean?>
        get() = _loveButtonLivedata

    init {
        coroutineScope.launch {
            val albumDeferred = repositoryUtils.fetchAlbumAsync(id)
            try {
                val albumVal = albumDeferred.await()
                if (albumVal != null) {
                    _album.postValue(albumVal)
                } else {
                    Timber.i("AlbumScreenViewModel-> Response albumVal value is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLovedStatusAsync(album: Album) {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.i("LoveButton -> getLovedStatusAsync passed value -> $album")
            val albumListDeferred = SubsonicApi.retrofitService.getStarred2Async()
            try {
                val root = albumListDeferred.await()
                var isLovedAlready = false
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.starred2?.album != null) {
                    val albumList = root.subsonicResponse.starred2.album
                    Timber.i("LoveButton -> ${albumList ?: "LoveButton-> albumList is null"}")
                    if (albumList != null) {

                        for (starredAlbums in albumList) {
                            if (starredAlbums.id == album.id) {
                                Timber.i("LoveButton -> $starredAlbums")
                                _loveButtonLivedata.postValue(true)
                                isLovedAlready = true
                            }
                        }
                    }

                } else if (root.subsonicResponse.status == "failed") {
                    //TODO(Log error messages using ErrorHandler Interface here)
                    Timber.i(
                        root.subsonicResponse.error?.message ?: "ERROR in getLovedStatusAsync()"
                    )
                }
                if (!isLovedAlready) {
                    _loveButtonLivedata.postValue(false)
                }
                Timber.i("LoveButton -> getLovedStatusAsync -> ${_loveButtonLivedata.value}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setLoveButtonLiveDataToNull() {
        _loveButtonLivedata.postValue(null)
    }

    fun unstarAlbum(b: Boolean, _id: String) {
        coroutineScope.launch {
            if (b) {
                Timber.i("LoveButton -> Unstarring album")
                val unstarAlbumDeferred =
                    SubsonicApi.retrofitService.unstarAlbumAsync(albumId = _id)
                try {
                    val root = unstarAlbumDeferred.await()
                    if (root.subsonicResponse.status != "failed") {
                        Timber.i("LoveButton -> Unstarring album successful")
                        _loveButtonLivedata.postValue(false)
                    } else {
                        Timber.i("LoveButton -> Unstarring album unsuccessful")
                        Timber.i(
                            "Error -> nowPlayingViewModel -> unstarTrack -> ${
                                ErrorHandler.logErrorMessage(
                                    subsonicResponseError = root.subsonicResponse.error
                                )
                            }"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Timber.i("LoveButton -> Starring album")
                val starAlbumDeferred = SubsonicApi.retrofitService.starAlbumAsync(albumId = _id)
                try {
                    val root = starAlbumDeferred.await()
                    if (root.subsonicResponse.status != "failed") {
                        Timber.i("LoveButton -> Starring album successful")
                        _loveButtonLivedata.postValue(true)
                    } else {
                        Timber.i("LoveButton -> Starring album unsuccessful")
                        Timber.i(
                            "Error -> nowPlayingViewModel -> unstarTrack -> ${
                                ErrorHandler.logErrorMessage(
                                    subsonicResponseError = root.subsonicResponse.error
                                )
                            }"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}