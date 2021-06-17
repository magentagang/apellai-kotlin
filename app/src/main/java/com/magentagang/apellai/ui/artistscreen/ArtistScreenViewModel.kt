package com.magentagang.apellai.ui.artistscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ArtistScreenViewModel(application: Application, id : String) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)
    val viewModelJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val _artist = MutableLiveData<Artist?>()
    val artist: LiveData<Artist?>
        get() = _artist

    init {
        coroutineScope.launch {
            val artistDeferred = repositoryUtils.fetchArtistAsync(id)
            try{
                val artistVal = artistDeferred.await()
                if(artistVal != null){
                    _artist.postValue(artistVal)
                }else{
                    Timber.i("AlbumScreenViewModel-> Response albumVal value is null")
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}