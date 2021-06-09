package com.magentagang.apellai.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "album_table")
data class Album (
    @PrimaryKey var id : String = "",
    @ColumnInfo(name = "name") var name : String = "Unknown Album",
    @ColumnInfo(name = "artist") var artist : String = "Unknown Artist",
    @ColumnInfo(name = "artistId") var artistId : String = "",
    @ColumnInfo(name = "songCount") var songCount : Int? = null,
    @ColumnInfo(name = "duration") var duration : Int? = null,
    @ColumnInfo(name = "created") var created : String? = null,
    @ColumnInfo(name = "year") var year : Int? = null,
    //TODO(add column info)
    var parent : String? = "",
    var isDir : Boolean? = null,
    var title : String? = "",
    var genre : String? = "", // add to database
    var coverArt : String? = "", // add to database
    var isVideo : Boolean? = null,
    var album : String? = "",
    var starred : String? = null, // add to database, manually add on callback
    var isRandom : Boolean = false, // add to database, manually add on callback
    var isNewest : Boolean = false, // add to database, manually add on callback
    var isHighest : Boolean = false, // add to database, manually add on callback
    var isFrequent : Boolean = false, // add to database, manually add on callback
    var isRecent : Boolean = false, // add to database, manually add on callback
    @Ignore
    @Json(name = "song") var songList : List<Track> = ArrayList(),
    //parent, isDir, title, genre, coverArt, isVideo, album
)