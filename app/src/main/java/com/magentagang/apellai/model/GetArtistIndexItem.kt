package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class GetArtistIndexItem(
    val name: String?,
    @Json(name = "artist") val artistList: List<Artist>,
)
