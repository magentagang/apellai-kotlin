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
    var starred : String? = null,
    @Ignore
    @Json(name = "song") var songList : List<Track> = ArrayList(),
)