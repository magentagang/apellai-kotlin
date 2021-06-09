package com.magentagang.apellai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
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
//        getArtists()
//        getStreamBinary("f408df38cb3ca7f472d18f6b1d64f8dc")
//        getArtist()
//        starSong("7b365b733b6ceab00b7cfb8d710bb6e4")
//        unstarArtist("49122de0a36069f001e7e3d568f3339e")
//        unStarAlbum("c225844106c090c7f3237b964331fc99")
//        getStarred()
//        search("never")
          getAlbumList()
    }
    // test codes
    fun getAlbum(_id: String = "f76fcdde71a3708aa45de4fc841773aa") {
        Timber.i("Function getAlbumList Called")
        coroutineScope.launch {
            val getAlbumListDeferred =
                SubsonicApi.retrofitService.getAlbumAsync(id = _id)
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


    fun getArtist(_id: String = "b56870783b82e1413af57d4cbde24f31") {
        Timber.i("Function getArtist Called")
        coroutineScope.launch {
            val getArtistDeferred =
                SubsonicApi.retrofitService.getArtistAsync(id = _id)
            try {
                val root = getArtistDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    _status =
                        "Success ${root.subsonicResponse.artist?.toString()}"
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

    fun getArtists() {
        Timber.i("Function getArtist Called")
        coroutineScope.launch {
            val getArtistsListDeferred =
                SubsonicApi.retrofitService.getArtistsAsync()
            try {
                val root = getArtistsListDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    _status =
                        "Success ${root.subsonicResponse.getArtistArtists!!.indices.size}"
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

    private suspend fun insertArtist(artist: Artist) {
        return withContext(Dispatchers.IO) {
            Timber.i("insertArtist() started")
            databaseDao.insertArtist(artist)
        }
    }

    fun starSong(_id : String){
        coroutineScope.launch {
            val getStarredDeferred = SubsonicApi.retrofitService.starSongAsync(id = _id)
            try {
                val root = getStarredDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    _status =
                        "Success"
                } else {
                    _status =
                        "Status was not ok ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                _status = "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }

    private fun unstarArtist(_id : String){
        coroutineScope.launch {
            val getStarredDeferred = SubsonicApi.retrofitService.unstarArtistAsync(artistId = _id)
            _status = try {
                val root = getStarredDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    "Success"
                } else {
                    "Status was not ok  ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }


    private fun unStarAlbum(_id : String) {
        coroutineScope.launch {
            val starredAlbumDeferred = SubsonicApi.retrofitService.unstarAlbumAsync(albumId = _id)
            _status = try {
                val root = starredAlbumDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    "Success "
                } else {
                    "Status was not ok  ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }


    private fun getStarred() {
        coroutineScope.launch {
            val getStarredDeferred = SubsonicApi.retrofitService.getStarred2Async()
            _status = try {
                val root = getStarredDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    "Success ${root.subsonicResponse.starred2.toString()}"
                } else {
                    "Status was not ok  ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }

    private fun search(_query: String){
        coroutineScope.launch {
            val queryDeferred = SubsonicApi.retrofitService.search3Async(query = _query)
            _status = try {
                val root = queryDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    "Success ${root.subsonicResponse.searchResult3.toString()}"
                } else {
                    "Status was not ok  ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }

    private fun getAlbumList(){
        Timber.i( "getAlbumList was called")
        coroutineScope.launch {
            val getAlbumListDeferred = SubsonicApi.retrofitService.getAlbumListAsync()
            _status = try {
                val root = getAlbumListDeferred.await()
                Timber.i("Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status == "ok") {
                    "Success ${root.subsonicResponse.albumRoot.toString()}"
                } else {
                    "Status was not ok  ${root.subsonicResponse.error.toString()}"
                }
            } catch (e: Exception) {
                "Failure: ${e.message}"
            }
            Timber.i(_status)
        }
    }

//    fun playMusic(_id : String = "f408df38cb3ca7f472d18f6b1d64f8dc"){
//        // Music player start on Button click
//        val mediaPlayer = MediaPlayer().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build()
//            )
//            getStreamBinary(_id)?.let { setDataSource(it) } ?: Timber.i("ERROR IN setDataSource")
//            prepare() // might take long! (for buffering, etc)
//            start()
//        }
//    }
//
//    private fun getStreamBinary(_id: String) {
//        Timber.i("getStreamBinary Entered")
//        var root: AssetFileDescriptor? = null
//        coroutineScope.launch {
//            Timber.i("coroutineScope launched")
//            val getAssetFileDescriptor =
//                SubsonicApi.retrofitService.getStreamBinaryAsync(id = _id)
//            Timber.i(getAssetFileDescriptor.toString())
//            try {
//                root = getAssetFileDescriptor.await()
//                Timber.i("getStreamBinary await over")
//                if (root == null) {
//                    Timber.i("music data source null")
//                } else {
////                    val mediaPlayer = MediaPlayer().apply {
////                        setAudioAttributes(
////                            AudioAttributes.Builder()
////                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
////                                .setUsage(AudioAttributes.USAGE_MEDIA)
////                                .build()
////                        )
////                        setDataSource(root!!)
////                        prepare() // might take long! (for buffering, etc)
////                        start()
////                    }
//                    Timber.i("Bhai Dhuksi")
//                }
//
//            } catch (e: IllegalStateException) {
//                e.printStackTrace()
//            } catch(e : IllegalArgumentException){
//                e.printStackTrace()
//            } catch (e : IOException){
//                e.printStackTrace()
//            }
//        }
//    }

    //


    override fun onStart() {
        super.onStart()
        Timber.i("on Start was invoked")
    }
}