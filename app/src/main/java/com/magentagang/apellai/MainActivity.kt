package com.magentagang.apellai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.model.Track
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import org.json.JSONArray
import timber.log.Timber


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

        // TODO(INSERT SEARCH HISTORY THIS WAY)
        // val searchHistory2 = SearchHistory("butum", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        //TODO(IT SHOULD RUN ONLY ONCE, DON'T KNOW WHERE TO PUT THE CODE)
        CoroutineScope(Dispatchers.IO).launch {
            Timber.i("Coroutine was launched")
            val username = Constants.USER
            val password =  "randomPassword" //wyT*6f7tzgSTU#s^k4L^
            val boolDeferred = repositoryUtils.authenticate(_username = username, _password = password)
            val albumDeferred = repositoryUtils.fetchAlbumAsync(Constants.ALBUM_ID)
            repositoryUtils.storeCredentials(username, password)

            val moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Track> = moshi.adapter<Track>(Track::class.java)
            try{
                val boolActual = boolDeferred.await()
                val album = albumDeferred.await()
                val tracks = album?.songList
                if(tracks == null) Timber.i("JSON: TRACKS ARE NULL")
                // json array converter stuff
                val jsonArray = JSONArray()
                if (tracks != null) {
                    for(track in tracks){
                        val json = jsonAdapter.toJson(track)
                        jsonArray.put(json)
                    }
                }
                Timber.i("JSON: $jsonArray")
                Timber.i("-------------------------------------------------------------")
                val jsonArray2 = JSONArray(jsonArray.toString())
                for(i in 0 until jsonArray2.length()){
                    val track = jsonAdapter.fromJson(jsonArray2.get(i).toString())
                    Timber.i("JSON: trackId -> $track")
                }

                Timber.i("Authenticate : $boolActual with values u: $username, p: $password")
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