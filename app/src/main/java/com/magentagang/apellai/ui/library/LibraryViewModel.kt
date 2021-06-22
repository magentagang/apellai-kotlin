package com.magentagang.apellai.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.*

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    var repositoryUtils: RepositoryUtils

    init {
        repositoryUtils = RepositoryUtils(databaseDao)
    }

    fun initializeLibraryAsync(): Deferred<Boolean> {
        return viewModelScope.async {
            try {
                repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
                repositoryUtils.retrieveAllArtists()
                return@async true
            } catch (e: Exception) {
                e.printStackTrace()
                return@async false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}