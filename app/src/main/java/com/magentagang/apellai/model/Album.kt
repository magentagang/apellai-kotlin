package com.magentagang.apellai.model

data class Album (
    val id: String,
    val name: String = "Unknown Album",
    val artist: String = "Unknown Artist",
    val artistId: String? = null,
    val songCount: Int? = null,
    val duration: Int? = null,
    val created: String? = null,
    val song: List<Track>? = null,
    val year: Int = 0,
)