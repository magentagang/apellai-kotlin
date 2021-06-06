package com.magentagang.apellai.model

data class Artist (
    val id: String,
    val name: String = "Unknown Artist",
    val albumCount: Int? = null,
    val album: List<Album>? = null,
)