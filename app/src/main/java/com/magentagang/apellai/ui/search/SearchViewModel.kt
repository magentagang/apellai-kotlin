package com.magentagang.apellai.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.model.SubsonicResponseRoot
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

@ExperimentalCoroutinesApi
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    val searchQuery = MutableStateFlow("")
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)
    var albums = MutableLiveData<List<Album>>()
    var artists = MutableLiveData<List<Artist>>()
    var tracks = MutableLiveData<List<Track>>()
    var subsonicResponseRoot: LiveData<SubsonicResponseRoot>

    init {

        val subsonicResponseRootFlow = searchQuery.flatMapLatest {
            repositoryUtils.fetchSearchResultFlow(it)
        }
        repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)

        subsonicResponseRoot = subsonicResponseRootFlow.asLiveData()
    }

    private val _navigateToAlbumScreen: MutableLiveData<String?> = MutableLiveData<String?>()
    val navigateToAlbumScreen
        get() = _navigateToAlbumScreen

    private val _navigateToArtistScreen = MutableLiveData<String?>()
    val navigateToArtistScreen
        get() = _navigateToArtistScreen

    fun onAlbumClicked(id: String) {
        _navigateToAlbumScreen.value = id
    }

    fun onArtistClicked(id: String) {
        _navigateToArtistScreen.value = id
    }

    fun doneNavigating() {
        _navigateToAlbumScreen.value = null
        _navigateToArtistScreen.value = null
    }


}