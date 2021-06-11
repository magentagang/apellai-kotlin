package com.magentagang.apellai.repository

import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Server
import com.magentagang.apellai.model.User
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.service.SubsonicApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

const val ALBUM_SIZE_PER_CALL = 400

class RepositoryUtils(private val databaseDao: DatabaseDao) {

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


    var job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)


    // insertServer to be called from main
    fun insertServer(server: Server){
        coroutineScope.launch {
            insertServerSuspend(server)
        }
    }

    // insertUser to be called from  main
    fun insertUser(user:User){
        coroutineScope.launch {
            insertUserSuspend(user)
        }
    }

    // Insert a new Server information to the database
    suspend fun insertServerSuspend(server: Server) {
        return withContext(Dispatchers.IO) {
            Timber.i("insertServer() started")
            databaseDao.insertServer(server)
        }
    }

    // Insert a new User information to the database
    suspend fun insertUserSuspend(user: User) {
        return withContext(Dispatchers.IO) {
            Timber.i("insertUser() started")
            databaseDao.insertUser(user)
        }
    }

    fun retrieveAllAbums(type: String, genre: String = "", musicFolderId: String = "") {
        //TODO(MIGHT NEED TO DO ALL THE MANUAL RESETING OF isTHIS is THAT here)
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

    // Retrieves and inserts the list of retrieved albums to the database, suspending function
    private suspend fun retrieveAlbumChunk(
        _type: String, _size: Int = 20, _offset: Int = 0,
        _genre: String = "", _musicFolderId: String = ""
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
                if (root.subsonicResponse.albumRoot != null) {
                    result = root.subsonicResponse.albumRoot.albumListItemList
                    Timber.i("retrieveAlbumChunk -> returned ${result!!.size} items. Offset(${_offset}).")
                    if (result!!.size == ALBUM_SIZE_PER_CALL) {
                        flag = true
                    }
                    for (album in result!!) {
                        // insert if it didn't exist, update if it did
                        if (databaseDao.getAlbum(album.id) == null) {
                            insertAlbum(album, _type)
                        } else {
                            updateAlbumType(album, _type)
                        }
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
        Timber.i("retrieveAlbumChunk -> RETURNING $isNextCallPossibleDeferred items. Offset(${_offset}).")
        return isNextCallPossible
    }

    // Updates album type infos in room database
    private suspend fun updateAlbumType(album: Album, type: String) {
        return withContext(Dispatchers.IO) {
            Timber.i("updateAlbum started")
            when (type.lowercase()) {
                "recent" -> databaseDao.updateAlbumMakeRecent(album.id)
                "random" -> databaseDao.updateAlbumMakeRandom(album.id)
                "starred" -> databaseDao.updateAlbumMakeStarred(album.id)
                "frequent" -> databaseDao.updateAlbumMakeFrequent(album.id)
                "highest" -> databaseDao.updateAlbumMakeHighest(album.id)
                else -> databaseDao.updateAlbumMakeNewewst(album.id)
            }
        }
    }


    //  Inserts a single album to the room database
    private suspend fun insertAlbum(album: Album, type: String) {
        return withContext(Dispatchers.IO) {
            Timber.i("insertAlbum() started")
            when (type.lowercase()) {
                "recent" -> album.isRecent = true
                "random" -> album.isRandom = true
                "starred" -> album.isStarred = true
                "frequent" -> album.isFrequent = true
                "highest" -> album.isHighest = true
                else -> album.isNewest = true
            }
            databaseDao.insertAlbum(album)
        }
    }

}