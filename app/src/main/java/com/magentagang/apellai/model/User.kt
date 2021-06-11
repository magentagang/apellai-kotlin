package com.magentagang.apellai.model

import androidx.room.*

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null, //TODO(check if implementation is correct)
    @ColumnInfo
    val server_id: String? = null,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val password: String? = null,
    @ColumnInfo
    val salt: String? = null,
    @ColumnInfo
    val token: String? = null,
    @ColumnInfo
    val isActive: Boolean = false,
)
