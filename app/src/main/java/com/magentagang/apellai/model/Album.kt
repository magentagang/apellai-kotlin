package com.magentagang.apellai.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "album_table")
data class Album(
    @PrimaryKey var id: String = "",
    @ColumnInfo(name = "name") var name: String = "Unknown Album",
    @ColumnInfo(name = "artist") var artist: String = "Unknown Artist",
    @ColumnInfo(name = "artistId") var artistId: String = "",
    @ColumnInfo(name = "songCount") var songCount: Int? = null,
    @ColumnInfo(name = "duration") var duration: Int? = null,
    @ColumnInfo(name = "created") var created: String? = null,
    @ColumnInfo(name = "year") var year: Int? = null,
    var parent: String? = "",
    @ColumnInfo(name = "isDir") var isDir: Boolean? = null,
    var title: String? = "",
    @ColumnInfo(name = "genre") var genre: String? = "", // add to database
    var coverArt: String? = "", // add to database
    var isVideo: Boolean? = null,
    var album: String? = "",
    @ColumnInfo(name = "starred") var starred: String? = null, // add to database, manually add on callback
    @ColumnInfo(name = "isRandom") var isRandom: Boolean = false, // add to database, manually add on callback
    @ColumnInfo(name = "isNewest") var isNewest: Boolean = false, // add to database, manually add on callback
    @ColumnInfo(name = "isHighest") var isHighest: Boolean = false, // add to database, manually add on callback
    @ColumnInfo(name = "isFrequent") var isFrequent: Boolean = false, // add to database, manually add on callback
    @ColumnInfo(name = "isRecent") var isRecent: Boolean = false, // add to database, manually add on callback
    @ColumnInfo(name = "isStarred") var isStarred: Boolean = false, // add to database, manually add on callback
    @Ignore
    @Json(name = "song") var songList: List<Track> = ArrayList(),
)