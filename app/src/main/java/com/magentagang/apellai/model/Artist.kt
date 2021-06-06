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
    @Ignore var album : List<Album>? = null
)