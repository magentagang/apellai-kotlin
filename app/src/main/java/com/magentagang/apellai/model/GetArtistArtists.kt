package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class GetArtistArtists(
    @Json(name = "index") val indices:List<GetArtistIndexItem>,
    val lastModified : Long?,
    val ignoredArticles : String?,
)
