package com.magentagang.apellai.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "search_history_table")
data class SearchHistory(
    @PrimaryKey var searchQuery: String,
    @ColumnInfo(name = "searchTime")
    @TypeConverters(TimestampConverter::class) var searchTime: String,
)