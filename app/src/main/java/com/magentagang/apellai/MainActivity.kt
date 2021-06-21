package com.magentagang.apellai

import android.media.AudioManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        volumeControlStream = AudioManager.STREAM_MUSIC

        // test codes
        val application = requireNotNull(this).application
        databaseDao = UserDatabase.getInstance(application).databaseDao()
        val repositoryUtils = RepositoryUtils(databaseDao)

        // TODO(INSERT SEARCH HISTORY THIS WAY)
        // val searchHistory2 = SearchHistory("butum", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
        // TODO(IT SHOULD RUN ONLY ONCE, DON'T KNOW WHERE TO PUT THE CODE)
        initializeCategories(repositoryUtils, databaseDao)

        val fragmentManager = supportFragmentManager
        val nowPlayingMiniFragment = fragmentManager.findFragmentById(R.id.nowPlayingMini)

        // Extremely hacky implementation since setCustomAnimations doesn't seem to work on fragment hide
        val exitAnimation = AnimationUtils.loadAnimation(this, R.anim.pop_out_down) .apply {
            duration = 500
            setAnimationListener(object : AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    fragmentManager.beginTransaction()
                        .hide(nowPlayingMiniFragment!!)
                        .commit()
                }

                override fun onAnimationStart(animation: Animation?) {}
            })
        }


        showNowPlayingMini.observe(this, {
            when(it) {
                false -> {
                    nowPlayingMiniFragment?.let {
                        nowPlayingMiniFragment.view?.startAnimation(exitAnimation)
                    }
                }
                true -> {
                    nowPlayingMiniFragment?.let {
                        fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.pop_in_up, R.anim.pop_in_down, R.anim.pop_in_up, R.anim.pop_in_down)
                            .show(nowPlayingMiniFragment)
                            .commit()
                    }
                }
            }
        })

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

    companion object {
        val showNowPlayingMini = MutableLiveData<Boolean>()
            .apply { postValue(false) }
    }
}