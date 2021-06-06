package com.magentagang.apellai.model

import com.squareup.moshi.Json

data class SubsonicResponseRoot(
    @Json(name = "subsonic-response") val subsonicResponse: SubsonicResponse
    )
