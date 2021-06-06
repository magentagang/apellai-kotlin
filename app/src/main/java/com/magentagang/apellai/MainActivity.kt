package com.magentagang.apellai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.repository.service.SubsonicApi
import kotlinx.coroutines.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    // test codes
    var viewModelJob = Job()
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Default)
    var _status: String = "NONE"
    lateinit var databaseDao: DatabaseDao
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("on Create was invoked")
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations  .
        // peepeepooopoo radi branch
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_search, R.id.navigation_library))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //TODO(RUN THE APP AND CHECK IF IT'S BLOCKING UI THREAD)
        // test codes
        val application = requireNotNull(this).application
        databaseDao = UserDatabase.getInstance(application).databaseDao()
        Timber.i("Outside call")
        getAlbum()
        //
    }

    // test codes
    fun getAlbum() {
        Timber.i("Function getAlbumList Called")
        coroutineScope.launch {
            val getAlbumListDeferred =
                SubsonicApi.retrofitService.getAlbumAsync(id = "f76fcdde71a3708aa45de4fc841773aa")
            try {
                val root = getAlbumListDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    _status =
                        "Success ${root.subsonicResponse.album?.songList.toString()}"
                } else {
                    _status =
                        "Status was not ok"
                }
            } catch (e: Exception) {
                _status = "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }


    private suspend fun insertAlbum(album: Album) {
        return withContext(Dispatchers.IO) {
            Timber.i("insertAlbum() started")
            databaseDao.insertAlbum(album)
        }
    }

    //


    override fun onStart() {
        super.onStart()
        Timber.i("on Start was invoked")
    }
}