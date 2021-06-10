package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class AlbumListRoot(
    @Json(name = "album") val albumListItemList: List<Album>
)
