package com.magentagang.apellai.repository.database

import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.model.Track
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(track: Track)

    @Query("SELECT * FROM album_table WHERE id = :key")
    fun getAlbum(key : Long) : Album?

    @Query("SELECT * FROM artist_table WHERE id = :key")
    fun getArtist(key : Long) : Artist?

    @Query("SELECT * FROM track_table WHERE id = :key")
    fun getSong(key : Long) : Track?

    @Query("SELECT * FROM album_table")
    fun getAllAlbums(): List<Album>?

    @Query("SELECT * FROM artist_table")
    fun getAllArtists(): List<Artist>?

    @Query("SELECT * FROM track_table")
    fun getAllSongs(): List<Track>?

    @Query("DELETE FROM album_table WHERE id = :key")
    fun deleteDatabaseAlbum(key : Long)

    @Query("DELETE FROM artist_table WHERE id = :key")
    fun deleteDatabaseArtist(key : Long)

    @Query("DELETE FROM track_table WHERE id = :key")
    fun deleteDatabaseSong(key : Long)

    @Query("DELETE FROM album_table")
    fun clearAlbums()

    @Query("DELETE  FROM artist_table")
    fun clearArtists()

    @Query("DELETE  FROM track_table")
    fun clearSongs()
}