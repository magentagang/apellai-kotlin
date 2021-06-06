package com.magentagang.apellai

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.magentagang.apellai.repository.service.SubsonicApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    // test codes
    var viewModelJob = Job()
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    var _status: String = "NONE"
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("on Create was invoked")
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations  .
        // peepeepooopoo radi branch
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_search, R.id.navigation_library))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // test codes
        getAlbumList()
        //
    }

    // test codes
    fun getAlbumList() {
        Timber.i("Function getAlbumList Called")
        coroutineScope.launch {
            val getAlbumListDeferred = SubsonicApi.retrofitService.getAlbumListAsync(size = 17, offset = 100)
            try {
                val root = getAlbumListDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    _status =
                        "Success ${root.subsonicResponse.albumRoot!!.albumListItemList.size} Albums Retrieved"
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
    //

    override fun onStart() {
        super.onStart()
        Timber.i("on Start was invoked")
    }
}