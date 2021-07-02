package com.magentagang.apellai.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)


    // Function is async because I want the UI to show refreshing indicator
    // the whole time the initialize catagory func is running, so async is used to make the caller
    // coroutine wait for response

    fun initializeCategories(): Deferred<Boolean> {
        return viewModelScope.async {
            try {
                databaseDao.resetRandomAlbums()
                databaseDao.resetFrequentAlbums()
                databaseDao.resetRecentAlbums()
                databaseDao.resetNewestAlbums()
                repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RANDOM)
                repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RECENT)
                repositoryUtils.fetchCategorizedChunk(Constants.TYPE_FREQUENT)
                repositoryUtils.fetchCategorizedChunk(Constants.TYPE_NEWEST)
                repositoryUtils.retrieveAndStarAllAlbums()
                return@async true
            } catch (e: Exception) {
                e.printStackTrace()
                return@async false
            }
        }
    }

    fun logOutUser() {
        CoroutineScope(Dispatchers.IO).launch {
            databaseDao.resetAllActiveUser()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}