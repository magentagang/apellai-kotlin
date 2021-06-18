package com.magentagang.apellai.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.SubsonicResponseRoot
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

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