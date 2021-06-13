package com.magentagang.apellai.repository

import com.magentagang.apellai.model.*
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.service.SubsonicApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

const val ALBUM_SIZE_PER_CALL = 400
const val DEFAULT_CATEGORY_SIZE = 10

class RepositoryUtils(private val databaseDao: DatabaseDao) {


    var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)


    companion object {

//            TODO(Finish getUrlForStream() using stored credentials)
//        fun getUrlForStream(): String {
//            // build URI and URL
//            val uri = Uri.parse("https://apellai.duckdns.org").buildUpon().apply {
//                // Append path first
//                appendPath("rest")
//                appendPath("stream")
//                // Add required queries
//                appendQueryParameter("c", SubsonicApiService.CLIENT)
//                appendQueryParameter("u", APIInfo.user)
//                appendQueryParameter("v", APIInfo.version)
//                // Authorization related params
//                // Authorization related params
//                appendQueryParameter("s", APIInfo.salt)
//                appendQueryParameter("t", APIInfo.token)
//                // song identifier
//                appendQueryParameter("id", "bba4ce135a5f3a3ce32002ec4cd0fdc5")
//            }
//            return uri.toString()
//        }

        fun getMd5(input: String): String? {
            return try {
                // Static getInstance method is called with hashing MD5
                val md: MessageDigest = MessageDigest.getInstance("MD5")

                // digest() method is called to calculate message digest
                //  of an input digest() return array of byte
                val messageDigest: ByteArray = md.digest(input.toByteArray())

                // Convert byte array into signum representation
                val no = BigInteger(1, messageDigest)

                // Convert message digest into hex value
                var hashtext: String = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                hashtext
            } // For specifying wrong message digest algorithms
            catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
    }


    // insertServer to be called from main
    fun insertServer(server: Server) {
        coroutineScope.launch {
            insertServerSuspend(server)
        }
    }

    // insertUser to be called from  main
    fun insertUser(user: User) {
        coroutineScope.launch {
            insertUserSuspend(user)
        }
    }

    // Insert a new Server information to the database
    private suspend fun insertServerSuspend(server: Server) {
        return withContext(Dispatchers.IO) {
//            Timber.i("insertServer() started")
            databaseDao.insertServer(server)
        }
    }

    // Insert a new User information to the database
    suspend fun insertUserSuspend(user: User) {
        return withContext(Dispatchers.IO) {
//            Timber.i("insertUser() started")
            databaseDao.insertUser(user)
        }
    }


    // Function to be called to retrieve all album data
    fun retrieveAllAlbums(type: String, genre: String = "", musicFolderId: String = "") {
        //TODO(MIGHT NEED TO DO ALL THE MANUAL RESETTING OF isTHIS is THAT here)
        coroutineScope.launch {
            var nextCall = true
            var offset = 0
            while (nextCall) {
                nextCall = retrieveAlbumChunk(
                    _type = type, _genre = genre, _musicFolderId = musicFolderId,
                    _size = ALBUM_SIZE_PER_CALL, _offset = offset
                )
                offset += ALBUM_SIZE_PER_CALL
                Timber.i("New offset val: $offset")
            }
        }
    }

    // Function to be called to retrieve all artist data
    fun retrieveAllArtists(_musicFolderId: String = ""){
        coroutineScope.launch {
            val artistListDeferred = SubsonicApi.retrofitService.getArtistsAsync(musicFolderId = _musicFolderId)
            try{
                val root = artistListDeferred.await()
                Timber.i("retrieveArtistChunk -> Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                val indices = root.subsonicResponse.getArtistArtists?.indices
                if (indices != null) {
                    for(index in indices){
                        val artistList = index.artistList
                        for(artist in artistList){
                            //TODO(SEE IF THIS IS APPROPRIATE FOR ARTIST INSERTION)
                            databaseDao.insertOrIgnoreArtist(artist)
                        }
                    }
                }else{
                    Timber.i("Indices is null in retrieveAllArtists()")
                }
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    // Retrieves a list of starred albums and stars them in database
    // Although it doesn't matter, this function is called when retrieving/inserting entire album list is finished

    fun retrieveAndStarAllAlbums() {
        coroutineScope.launch {
            Timber.i("retrieveAndStarAllAlbums() started")
            val starredAlbumsDeferred = SubsonicApi.retrofitService.getStarred2Async()
            try {
                val root = starredAlbumsDeferred.await()
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.starred2?.album != null) {
                    val starredAlbumList = root.subsonicResponse.starred2.album
                    for (album in starredAlbumList!!) {
                        if (databaseDao.getAlbum(album.id) == null) {
                            insertAlbum(album, Constants.TYPE_STARRED)
                        } else {
                            updateAlbumType(album, Constants.TYPE_STARRED)
                        }
                    }
                }
            } catch (e: Exception) { // TODO(Error handling correct implementations)
                e.printStackTrace()
            }
        }
    }

    // Retrieves a list of starred artists and stars them in database

    fun retrieveAndStarAllArtists(){
        coroutineScope.launch {
            Timber.i("retrieveAndStarAllArtists() started")
            val starredAlbumsDeferred = SubsonicApi.retrofitService.getStarred2Async()
            try {
                val root = starredAlbumsDeferred.await()
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.starred2?.artist != null) {
                    val starredArtistList = root.subsonicResponse.starred2.artist
                    for (artist in starredArtistList!!) {
//                        if (databaseDao.getArtist(artist.id) == null) {
//                            insertArtist(artist, Constants.TYPE_STARRED)
//                        } else {
//                            updateArtistType(artist, Constants.TYPE_STARRED)
//                        }
                        //TODO(CAN'T WE JUST INSERT IN THIS CASE?)
                        artist.isStarred = true
                        databaseDao.insertArtist(artist)
                    }
                }
            } catch (e: Exception) { // TODO(Error handling correct implementations)
                e.printStackTrace()
            }
        }
    }

    // Retrieves and inserts the list of retrieved albums to the database, suspending function
    private suspend fun retrieveAlbumChunk(
        _type: String,
        _size: Int = 20,
        _offset: Int = 0,
        _genre: String = "",
        _musicFolderId: String = ""
    ): Boolean {
        Timber.i("retrieveAlbumChunk() called with -> size: ${_size}, offset: $_offset")
        var result: List<Album>?
        var isNextCallPossible = false
        Timber.i("getAlbumList was called")
        // Creates an async function that retrieves albums, stores them, returns deferred value representing whether isNextCallPossible should be true
        val isNextCallPossibleDeferred = coroutineScope.async {
            var flag = false
            val getAlbumListDeferred = SubsonicApi.retrofitService.getAlbumListAsync(
                type = _type, size = _size, offset = _offset,
                genre = _genre, musicFolderId = _musicFolderId
            )
            try {
                val root = getAlbumListDeferred.await()
                Timber.i("retrieveAlbumChunk -> Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.albumRoot != null) {
                    result = root.subsonicResponse.albumRoot.albumListItemList
                    Timber.i("retrieveAlbumChunk -> returned ${result!!.size} items. Offset(${_offset}).")
                    for (album in result!!) {
                        // insert if it didn't exist, update if it did
                        if (databaseDao.getAlbum(album.id) == null) {
                            insertAlbum(album, _type)
                        } else {
                            updateAlbumType(album, _type)
                        }
                    }
                    if (result!!.size == ALBUM_SIZE_PER_CALL) {
                        flag = true
                    } else {
                        retrieveAndStarAllAlbums()
                    }
                } else {
                    Timber.i("LIST IS NULL WHEN -> retrieveAlbumChunk() called with -> size: ${_size}, offset: $_offset")
                }
            } catch (e: Exception) {
                //TODO(CORRECT EXCEPTION HANDLING)
                e.printStackTrace()
            }
            return@async flag
        }
        try {
            isNextCallPossible = isNextCallPossibleDeferred.await()
        } catch (e: Exception) {
            //TODO(CORRECT EXCEPTION HANDLING)
            e.printStackTrace()
        }
        Timber.i("retrieveAlbumChunk -> RETURNING $isNextCallPossibleDeferred.value items. Offset(${_offset}).")
        return isNextCallPossible
    }


    // fetch chunk of categorized albums
    fun fetchCategorizedChunk(
        _type: String
    ) {
        coroutineScope.launch {
            Timber.i("CALLING fetchCategorizedChunk")
            val albumListDeferred = SubsonicApi.retrofitService.getAlbumListAsync(
                type = _type, size = DEFAULT_CATEGORY_SIZE
            )
            val result: List<Album>?
            try {
                val root = albumListDeferred.await()
                Timber.i("fetchCategorizedChunk -> Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.albumRoot != null) {
                    result = root.subsonicResponse.albumRoot.albumListItemList
                    Timber.i("fetchCategorizedChunk -> returned ${result.size} items.")
                    for (album in result) {
                        // insert if it didn't exist, update if it did
//                        if (databaseDao.getAlbum(album.id) == null) {
//                            insertAlbum(album, _type)
//                        } else {
//                            updateAlbumType(album, _type)
//                        }
    // TODO(Check if its better than the commented out part above)
                        databaseDao.insertOrIgnoreAlbum(album)
                        updateAlbumType(album, _type)

                    }
                    Timber.i("Fetched all -> $_type")
                } else {
                    Timber.i("LIST IS NULL-> fetchCategorizedChunk")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Async fun for returning album data using getalbum, call in a coroutine
    suspend fun fetchAlbumAsync(_id: String):Deferred<Album?>{
        return coroutineScope.async {
            val albumDeferred = SubsonicApi.retrofitService.getAlbumAsync(id = _id)
            var album : Album? = null
            try{
                val root = albumDeferred.await()
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.album != null){
                    album = root.subsonicResponse.album
                }else {
                    Timber.i("No album response was found")
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
            return@async album
        }
    }

    // Async fun for returning album data using getalbum, call in a coroutine
    suspend fun fetchArtistAsync(_id: String):Deferred<Artist?>{
        return coroutineScope.async {
            val ArtistDeferred = SubsonicApi.retrofitService.getArtistAsync(id = _id)
            var Artist : Artist? = null
            try{
                val root = ArtistDeferred.await()
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.artist != null){
                    Artist = root.subsonicResponse.artist
                }else {
                    Timber.i("No Artist response was found")
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
            return@async Artist
        }
    }

    // Async fun for fetching track data, call in a coroutine
    suspend fun fetchTrackAsync(_id: String):Deferred<Track?>{
        return coroutineScope.async {
            val TrackDeferred = SubsonicApi.retrofitService.getTrackAsync(id = _id)
            var Track : Track? = null
            try{
                val root = TrackDeferred.await()
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.track != null){
                    Track = root.subsonicResponse.track
                }else {
                    Timber.i("No Track response was found")
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
            return@async Track
        }
    }


    // Updates album type info in room database
    private suspend fun updateAlbumType(album: Album, type: String) {
        return withContext(Dispatchers.IO) {
//            Timber.i("updateAlbum started")
            when (type.lowercase()) {
                Constants.TYPE_RECENT -> databaseDao.updateAlbumMakeRecent(album.id)
                Constants.TYPE_RANDOM -> databaseDao.updateAlbumMakeRandom(album.id)
                Constants.TYPE_STARRED -> databaseDao.updateAlbumMakeStarred(album.id)
                Constants.TYPE_FREQUENT -> databaseDao.updateAlbumMakeFrequent(album.id)
                Constants.TYPE_NEWEST -> databaseDao.updateAlbumMakeNewest(album.id)
            }
        }
    }


    //  Inserts a single album to the room database
    private suspend fun insertAlbum(album: Album, type: String) {
        return withContext(Dispatchers.IO) {
//            Timber.i("insertAlbum() started")
            when (type.lowercase()) {
                Constants.TYPE_RECENT -> album.isRecent = true
                Constants.TYPE_RANDOM -> album.isRandom = true
                Constants.TYPE_STARRED -> album.isStarred = true
                Constants.TYPE_FREQUENT -> album.isFrequent = true
                Constants.TYPE_NEWEST -> album.isNewest = true
            }
            databaseDao.insertAlbum(album)
        }
    }

}