package com.magentagang.apellai.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "artist_table")
data class Artist(
    @PrimaryKey var id : String = "",
    @ColumnInfo(name = "name") var name : String = "Unknown Artist",
    @ColumnInfo(name = "albumCount") var albumCount : Int? = null,
    //TODO(add column info)
    @ColumnInfo(name = "starred") var starred: String? = null, // add to database, manually add on callback
    @ColumnInfo(name = "isStarred") var isStarred: Boolean = false, // add to database, manually add on callback
    @Ignore var album : List<Album> = ArrayList(),

)