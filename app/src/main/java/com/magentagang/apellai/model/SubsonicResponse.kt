package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class SubsonicResponse(
    val status: String,
    val version: String, val type: String, val serverVersion: String,
    //TODO(ALL RESPONSE REQUEST PARSING OBJECTS)
    @Json(name = "albumList2") val albumRoot: AlbumListRoot?,
)
