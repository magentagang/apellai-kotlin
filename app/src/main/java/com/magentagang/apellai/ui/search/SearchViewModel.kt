package com.magentagang.apellai.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.magentagang.apellai.model.SubsonicResponseRoot
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber

@ExperimentalCoroutinesApi
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    val searchQuery = MutableStateFlow("")
    var repositoryUtils: RepositoryUtils
    var subsonicResponseRoot:LiveData<SubsonicResponseRoot>
    init{
        repositoryUtils = RepositoryUtils(databaseDao)

        val subsonicResponseRootFlow = searchQuery.flatMapLatest {
            repositoryUtils.fetchSearchResultFlow(it)
        }

        subsonicResponseRoot = subsonicResponseRootFlow.asLiveData()
    }


}