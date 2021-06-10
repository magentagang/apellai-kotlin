package com.magentagang.apellai.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "track_table")
data class Track(
    @PrimaryKey var id: String = "",
    @ColumnInfo(name = "title", defaultValue = "Unknown Title") var title: String = "Unknown Title",
    @ColumnInfo(name = "album", defaultValue = "Unknown Album") var album: String = "Unknown Album",
    @ColumnInfo(
        name = "artist",
        defaultValue = "Unknown Artist"
    ) var artist: String = "Unknown Artist",
    @ColumnInfo(name = "track") var track: Int = 0,
    @ColumnInfo(name = "year") var year: Int = 0,
    @ColumnInfo(name = "duration") var duration: Int = 0,
    @ColumnInfo(name = "parent") var parent: String? = null,
    @ColumnInfo(name = "isDir") var isDir: Boolean? = null,
    @ColumnInfo(name = "genre") var genre: String? = null,
    @ColumnInfo(name = "coverArt") var coverArt: String? = null,
    @ColumnInfo(name = "size") var size: Long? = null,
    @ColumnInfo(name = "contentType") var contentType: String? = null,
    @ColumnInfo(name = "suffix") var suffix: String = "",
    @ColumnInfo(name = "bitrate") var bitRate: Int = 0,
    @ColumnInfo(name = "path") var path: String? = null,
    @ColumnInfo(name = "discNumber") var discNumber: Int? = null,
    @ColumnInfo(name = "created") var created: String? = null,
    @ColumnInfo(name = "albumId") var albumId: String = "",
    @ColumnInfo(name = "artistId") var artistId: String = "",
    @ColumnInfo(name = "type") var type: String? = null,
    @ColumnInfo(name = "isVideo") var isVideo: Boolean? = null,
    //TODO(add column info)
    var starred: String? = null,
)