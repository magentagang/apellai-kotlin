package com.magentagang.apellai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.model.SearchHistory
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.Instant
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var databaseDao: DatabaseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        // test codes
        val application = requireNotNull(this).application
        databaseDao = UserDatabase.getInstance(application).databaseDao()
        val repositoryUtils = RepositoryUtils(databaseDao)
        //TODO(IT SHOULD RUN ONLY ONCE, DON'T KNOW WHERE TO PUT THE CODE)
        CoroutineScope(Dispatchers.IO).launch {
            Timber.i("Coroutine was launched")
            val searchHistory = SearchHistory("monow", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            val searchHistory2 = SearchHistory("butum", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            databaseDao.insertSearchHistory(searchHistory)
            databaseDao.insertSearchHistory(searchHistory2)
            val searchHistoryList = databaseDao.getRecentSearches()
           // databaseDao.clearSearchHistory()
            val searchHistoryListAfterDeleting = databaseDao.getRecentSearches()
            Timber.i("SEARCH HISTORY BEFORE DELETING:\n")
            for(listElements in searchHistoryList)
                Timber.i("%s %s", listElements.searchQuery, listElements.searchTime)
            Timber.i("SEARCH HISTORY AFTER DELETING:\n")
            for(listElements in searchHistoryListAfterDeleting)
                Timber.i("%s %s", listElements.searchQuery, listElements.searchTime)
            val albumDeferred = repositoryUtils.fetchAlbumAsync("f76fcdde71a3708aa45de4fc841773aa")
            val artistDeferred = repositoryUtils.fetchArtistAsync("49122de0a36069f001e7e3d568f3339e")
            val trackDeferred = repositoryUtils.fetchTrackAsync("f408df38cb3ca7f472d18f6b1d64f8dc")
            try{
                val album = albumDeferred.await()
                val artist = artistDeferred.await()
                val track = trackDeferred.await()

                Timber.i("ALBUM -? ${album.toString()}")
                Timber.i("ARTIST -? ${artist.toString()}")
                Timber.i("TRACK -? ${track.toString()}")

            }catch(e : Exception){
                e.printStackTrace()
            }
        }
        initializeCategories(repositoryUtils, databaseDao)
    }

    private fun initializeCategories(repositoryUtils: RepositoryUtils, databaseDao: DatabaseDao) {
        CoroutineScope(Dispatchers.IO).launch{
            databaseDao.resetRandomAlbums()
            databaseDao.resetFrequentAlbums()
            databaseDao.resetRecentAlbums()
            databaseDao.resetNewestAlbums()
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RANDOM)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RECENT)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_FREQUENT)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_NEWEST)
            repositoryUtils.retrieveAllArtists()
            repositoryUtils.retrieveAllAlbums(Constants.TYPE_ALPHABETICAL_BY_NAME)
            repositoryUtils.retrieveAndStarAllAlbums()
        }
    }


    override fun onStart() {
        super.onStart()
        Timber.i("on Start was invoked")
    }

    override fun onDestroy() {
        super.onDestroy()
        //TODO(DO WHAT?)
    }
}