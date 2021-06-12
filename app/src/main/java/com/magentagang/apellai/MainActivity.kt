package com.magentagang.apellai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.model.Constants
import com.magentagang.apellai.repository.RepositoryUtils
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        //TODO(IT SHOULD RUN ONLY ONCE, DON'T KNOW WHERE TO PUT THE CODE)
        initializeCategories(repositoryUtils, databaseDao)
    }

    private fun initializeCategories(repositoryUtils: RepositoryUtils, databaseDao: DatabaseDao) {
        CoroutineScope(Dispatchers.IO).launch{
            databaseDao.resetRandomAlbums()
            databaseDao.resetFrequentAlbums()
            databaseDao.resetRecentAlbums()
            databaseDao.resetNewestAlbums()
            repositoryUtils.retrieveAllArtists()
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RANDOM)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_RECENT)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_FREQUENT)
            repositoryUtils.fetchCategorizedChunk(Constants.TYPE_NEWEST)
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