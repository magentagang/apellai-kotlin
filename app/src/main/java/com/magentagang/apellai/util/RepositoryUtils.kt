package com.magentagang.apellai.util

import android.net.Uri
import android.widget.Toast
import com.magentagang.apellai.MainActivity
import com.magentagang.apellai.model.*
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.service.SubsonicApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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


        fun generateSalt(_length : Long) : String{
            var length = _length
            if(length < 6){
                println("Salt lenght needs to be atlease six characters")
                length = 6
            }
            val sourceOne = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val sourceTwo = sourceOne.lowercase()
            val sourceThree = "1234567890"
            val source = sourceOne + sourceThree + sourceTwo
            return java.util.Random().ints(length, 0, source.length)
                .toArray()
                .map(source::get)
                .joinToString("")
        }


         fun getCoverArtUrl(_id : String, _size: String = ""): String {
            // build URI and URL
            val uri = Uri.parse("https://apellai.duckdns.org").buildUpon().apply {
                // Append path first
                appendPath("rest")
                appendPath("getCoverArt")
                // Add required queries
                appendQueryParameter("c",  Constants.CLIENT)
                appendQueryParameter("u", Constants.USER)
                appendQueryParameter("v", Constants.VERSION)
                // Authorization related params
                // Authorization related params
                appendQueryParameter("s", Constants.SALT)
                appendQueryParameter("t", Constants.TOKEN)
                // song identifier
                appendQueryParameter("id", _id)
                appendQueryParameter("size", _size)
            }
            return uri.toString()
        }

        fun Track.getStreamUri(): Uri {
            return Uri.Builder()
                .scheme("https")
                .authority("apellai.duckdns.org")
                .appendPath("rest")
                .appendPath("stream")
                .appendQueryParameter("s", Constants.SALT)
                .appendQueryParameter("t", Constants.TOKEN)
                .appendQueryParameter("u", Constants.USER)
                .appendQueryParameter("c", Constants.CLIENT)
                .appendQueryParameter("v", Constants.VERSION)
                .appendQueryParameter("f", Constants.FORMAT)
                .appendQueryParameter("id", id)
                .build()
        }

        fun convertToHex(inputString : String): String
        {
            var outputString  = ""
            for(char in inputString)
            {
                outputString+=Integer.toHexString(char.code)
            }
            return outputString
        }

        fun generateToken(password : String, salt : String): String?
        {
            val outputToken = getMd5(password + salt)
            return outputToken
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
    private suspend fun insertUserSuspend(user: User) {
        return withContext(Dispatchers.IO) {
//            Timber.i("insertUser() started")
            databaseDao.insertUser(user)
        }
    }


    // Function to be called to retrieve all album data
    fun retrieveAllAlbums(type: String, genre: String = "", musicFolderId: String = "") {
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
    fun retrieveAllArtists(_musicFolderId: String = "") {
        coroutineScope.launch {
            val artistListDeferred =
                SubsonicApi.retrofitService.getArtistsAsync(musicFolderId = _musicFolderId)
            try {
                val root = artistListDeferred.await()
                Timber.i("retrieveArtistChunk -> Status: ${root.subsonicResponse.status}, Version: ${root.subsonicResponse.version}")
                val indices = root.subsonicResponse.getArtistArtists?.indices
                if (indices != null) {
                    for (index in indices) {
                        val artistList = index.artistList
                        for (artist in artistList) {
                            databaseDao.insertOrIgnoreArtist(artist)
                        }
                    }
                } else {
                    Timber.i("Indices is null in retrieveAllArtists()")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Retrieves a list of starred albums and stars them in database
    // Although it doesn't matter, this function is called when retrieving/inserting entire album list is finished

    suspend fun retrieveAndStarAllAlbums() {
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

    suspend fun retrieveAndStarAllArtists() {
        coroutineScope.launch {
            Timber.i("retrieveAndStarAllArtists() started")
            val starredAlbumsDeferred = SubsonicApi.retrofitService.getStarred2Async()
            try {
                val root = starredAlbumsDeferred.await()
                if (root.subsonicResponse.status != "failed" && root.subsonicResponse.starred2?.artist != null) {
                    val starredArtistList = root.subsonicResponse.starred2.artist
                    for (artist in starredArtistList!!) {
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

    // search result query


    fun fetchSearchResultFlow(_query: String): Flow<SubsonicResponseRoot> {
        return flow {
            // exectute API call and map to UI object
            val fooList = SubsonicApi.retrofitService.search3(query = _query)
            // Emit the list to the stream
            emit(fooList)
        }.catch { e -> e.printStackTrace() }.flowOn(Dispatchers.IO) // Use the IO thread for this Flow
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
            val artistDeferred = SubsonicApi.retrofitService.getArtistAsync(id = _id)
            var artist : Artist? = null
            try{
                val root = artistDeferred.await()
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.artist != null){
                    artist = root.subsonicResponse.artist
                }else {
                    Timber.i("No Artist response was found")
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
            return@async artist
        }
    }

    // Async fun for fetching track data, call in a coroutine
    suspend fun fetchTrackAsync(_id: String):Deferred<Track?>{
        return coroutineScope.async {
            val trackDeferred = SubsonicApi.retrofitService.getTrackAsync(id = _id)
            var track : Track? = null
            try{
                val root = trackDeferred.await()
                if(root.subsonicResponse.status != "failed" && root.subsonicResponse.track != null){
                    track = root.subsonicResponse.track
                }else {
                    Timber.i("No Track response was found")
                }
            }catch(e: Exception){
                e.printStackTrace()
            }
            return@async track
        }
    }

    // Async fun for version checking
    private suspend fun versionCheckAsync(): Deferred<String> {
        return coroutineScope.async {
            var version: String = "1.16.1"
            val versionDeferred = SubsonicApi.retrofitService.getPingForVersionAsync()
            try {
                val root = versionDeferred.await()
                version = root.subsonicResponse.version
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async version
        }
    }


    suspend fun authenticate(_username: String, _password: String): Deferred<Boolean> {
        return coroutineScope.async {
            val versionDeferred = versionCheckAsync()
            var isUser = false
            try {
                val version = versionDeferred.await()
                if (versionGreaterCheck(version)) {
                    Timber.i("Authenticate $version matched >.")
                    Constants.PASSWORD_HEX = ""
                    val _salt = generateSalt(_length = 7)
                    val _token = generateToken(password = _password, salt = _salt)
                    Timber.i("Authenticate password $_password, salt $_salt, token $_token")
                    val pingDeferred = SubsonicApi.retrofitService.getPingAsync(
                        user = _username,
                        salt = _salt,
                        token = _token ?: ""
                    )
                    try {
                        val root = pingDeferred.await()
                        if (root.subsonicResponse.status == "ok") {
                            isUser = true
                            Constants.SALT = _salt
                            Constants.USER = _username
                            Constants.TOKEN = _token!!
                        } else {
                            isUser = false
                        }
                    } catch (eInner: Exception) {
                        eInner.printStackTrace()
                    }
                } else {
                    Constants.SALT = ""
                    Constants.TOKEN = ""
                    val _passwordHex = convertToHex(_password)
                    val pingDeferred = SubsonicApi.retrofitService.getPingAsync(
                        user = _username,
                        passwordHex = _passwordHex
                    )
                    try {
                        val root = pingDeferred.await()
                        if (root.subsonicResponse.status == "ok") {
                            isUser = true
                            Constants.USER = _username
                            Constants.PASSWORD_HEX = _passwordHex
                        } else {
                            isUser = false
                        }
                    } catch (eInner: Exception) {
                        eInner.printStackTrace()
                    }
                }
            } catch (eOuter: Exception) {
                eOuter.printStackTrace()
            }
            return@async isUser
        }
    }

    private fun versionGreaterCheck(version: String): Boolean {
        val arr = version.split(".", ignoreCase = true)
        val first = arr.get(0).toInt()
        val second = arr.get(1).toInt()
        val check = first * 100 + second
        return (check > 112)
    }

}