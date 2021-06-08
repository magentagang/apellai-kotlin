package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class SubsonicResponse(
    val status: String,
    val version: String, val type: String, val serverVersion: String,
    //TODO(ALL RESPONSE REQUEST PARSING OBJECTS)
    @Json(name = "albumList2") val albumRoot: AlbumListRoot?,
    @Json(name = "album") val album: Album?,
    @Json(name = "artist") val artist : Artist?,
    @Json(name = "artists") val getArtistArtists: GetArtistArtists?,
    @Json(name = "searchResult3") val searchResult3: SearchResult3?,
    @Json(name = "error") val error: SubsonicResponseError?,
    @Json(name = "starred2") val starred2: Starred2?,
)
