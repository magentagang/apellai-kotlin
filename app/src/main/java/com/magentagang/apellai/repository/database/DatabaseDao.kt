package com.magentagang.apellai.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.model.Track

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(album: Album)

    @Query("UPDATE album_table SET isNewest = 1 where id = :key")
    fun updateAlbumMakeNewewst(key : String)

    @Query("UPDATE album_table SET isRandom = 1 where id = :key")
    fun updateAlbumMakeRandom(key : String)

    @Query("UPDATE album_table SET isRecent = 1 where id = :key")
    fun updateAlbumMakeRecent(key : String)

    @Query("UPDATE album_table SET isHighest = 1 where id = :key")
    fun updateAlbumMakeHighest(key : String)

    @Query("UPDATE album_table SET isStarred = 1 where id = :key")
    fun updateAlbumMakeStarred(key : String)

    @Query("UPDATE album_table SET isFrequent= 1 where id = :key")
    fun updateAlbumMakeFrequent(key : String)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(track: Track)

    @Query("SELECT * FROM album_table WHERE id = :key")
    fun getAlbum(key: String): Album?

    @Query("SELECT * FROM artist_table WHERE id = :key")
    fun getArtist(key : String) : Artist?

    @Query("SELECT * FROM track_table WHERE id = :key")
    fun getSong(key : String) : Track?

    @Query("SELECT * FROM album_table")
    fun getAllAlbums(): List<Album>?

    @Query("SELECT * FROM artist_table")
    fun getAllArtists(): List<Artist>?

    @Query("SELECT * FROM track_table")
    fun getAllSongs(): List<Track>?

    @Query("DELETE FROM album_table WHERE id = :key")
    fun deleteDatabaseAlbum(key : String)

    @Query("DELETE FROM artist_table WHERE id = :key")
    fun deleteDatabaseArtist(key : String)

    @Query("DELETE FROM track_table WHERE id = :key")
    fun deleteDatabaseSong(key : String)

    @Query("DELETE FROM album_table")
    fun clearAlbums()

    @Query("DELETE  FROM artist_table")
    fun clearArtists()

    @Query("DELETE  FROM track_table")
    fun clearSongs()
}