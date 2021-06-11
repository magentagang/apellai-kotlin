package com.magentagang.apellai.model

import androidx.room.*

@Entity(tableName = "server_table")
data class Server (
    @PrimaryKey
    val base_url : String, // temporarily
    @ColumnInfo
    val version : String,
    @ColumnInfo
    val type : String? = "",
    @ColumnInfo
    val server_version : String? = ""
)

