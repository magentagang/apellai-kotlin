package com.magentagang.apellai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.R
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import timber.log.Timber


class CardAlbumHScrollViewModel(application: Application, albumType : String) : AndroidViewModel(application) {
    var albums: LiveData<List<Album>>
    var databaseDao: DatabaseDao
    init{
        databaseDao = UserDatabase.getInstance(application).databaseDao()
        albums = databaseDao.getStarredAlbums().asLiveData()

        when(albumType)
        {
            application.getResources().getString(R.string.loved_albums) -> albums = databaseDao.getStarredAlbums().asLiveData()
            application.getResources().getString(R.string.most_played) -> albums = databaseDao.getStarredAlbums().asLiveData()//TODO etar method bana butum
            application.getResources().getString(R.string.recently_added) -> albums = databaseDao.getRecentAlbums().asLiveData()
            application.getResources().getString(R.string.random) -> albums = databaseDao.getRandomAlbums().asLiveData()
            else -> albums = databaseDao.getStarredAlbums().asLiveData()
        }


    }
}