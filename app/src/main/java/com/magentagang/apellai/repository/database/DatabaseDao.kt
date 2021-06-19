package com.magentagang.apellai.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magentagang.apellai.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertServer(server: Server)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM server_table WHERE base_url = :key")
    fun getServer(key: String): Server?

    @Query("SELECT * FROM user_table WHERE id = :key")
    fun getUser(key: String): User?

    @Query("UPDATE album_table SET isNewest = 1 where id = :key")
    fun updateAlbumMakeNewest(key: String)

    @Query("UPDATE album_table SET isRandom = 1 where id = :key")
    fun updateAlbumMakeRandom(key: String)

    @Query("UPDATE album_table SET isRecent = 1 where id = :key")
    fun updateAlbumMakeRecent(key: String)


    @Query("UPDATE album_table SET isStarred = 1 where id = :key")
    fun updateAlbumMakeStarred(key: String)

    @Query("UPDATE album_table SET isFrequent= 1 where id = :key")
    fun updateAlbumMakeFrequent(key: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(track: Track)

    @Query("SELECT * FROM album_table WHERE id = :key")
    fun getAlbum(key: String): Album?

    @Query("SELECT * FROM artist_table WHERE id = :key")
    fun getArtist(key: String): Artist?

    @Query("SELECT * FROM track_table WHERE id = :key")
    fun getSong(key: String): Track?

    // reset categories
    @Query("UPDATE album_table set isRandom = 0 where isRandom = 1")
    fun resetRandomAlbums()

    @Query("UPDATE album_table set isRecent = 0 where isRecent = 1")
    fun resetRecentAlbums()

    @Query("UPDATE album_table set isNewest = 0 where isNewest = 1")
    fun resetNewestAlbums()

    @Query("UPDATE album_table set isFrequent = 0 where isFrequent = 1")
    fun resetFrequentAlbums()


//    @Query("UPDATE album_table set isStarred = 0 where isStarred = 1")
//    fun resetStarredAlbums()


    @Query("DELETE FROM album_table WHERE id = :key")
    fun deleteDatabaseAlbum(key: String)

    @Query("DELETE FROM artist_table WHERE id = :key")
    fun deleteDatabaseArtist(key: String)

    @Query("DELETE FROM track_table WHERE id = :key")
    fun deleteDatabaseSong(key: String)

    @Query("DELETE FROM album_table")
    fun clearAlbums()

    @Query("DELETE  FROM artist_table")
    fun clearArtists()

    @Query("DELETE  FROM track_table")
    fun clearSongs()

    // live data
    @Query("SELECT * FROM album_table ORDER BY name ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM artist_table")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT * FROM track_table")
    fun getAllSongs(): Flow<List<Track>>

    @Query("SELECT * FROM album_table where isRandom = 1 LIMIT 10")
    fun getRandomAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM album_table where isNewest = 1 LIMIT 10")
    fun getNewestAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM album_table where isFrequent = 1 LIMIT 10")
    fun getFrequentAlbums(): Flow<List<Album>>


    @Query("SELECT * FROM album_table where isRecent = 1 LIMIT 10")
    fun getRecentAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM album_table where isStarred = 1 ORDER BY starred DESC")
    fun getStarredAlbums(): Flow<List<Album>>

    // Search related
    @Query("SELECT * FROM album_table WHERE name LIKE '%' || :searchQuery || '%'")
    fun getAlbumsSearch(searchQuery: String):Flow<List<Album>>

    @Query("SELECT * FROM search_history_table ORDER BY searchTime DESC LIMIT 5")
    fun getRecentSearches(): List<SearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchHistory(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history_table")
    fun clearSearchHistory()

    @Query("UPDATE user_table set isActive = 0 where isActive = 1")
    fun resetUser()
}