package com.magentagang.apellai.util

object Constants {
    const val TYPE_ALPHABETICAL_BY_NAME = "alphabeticalByName"
    const val TYPE_ALPHABETICAL_BY_ARTIST = "alphabeticalByArtist"
    const val TYPE_RECENT = "recent"
    const val TYPE_STARRED = "starred"
    const val TYPE_RANDOM = "random"
    const val TYPE_NEWEST = "newest"
    const val TYPE_FREQUENT = "frequent"

    // INFO TO BE DELETED LATER
    const val ALBUM_ID = "f76fcdde71a3708aa45de4fc841773aa"
    const val SONG_ID = "f408df38cb3ca7f472d18f6b1d64f8dc"
    const val DEFAULT_ALBUM_TYPE = "alphabeticalByName"
    const val ARTIST_ID = "b56870783b82e1413af57d4cbde24f31"

    // Authentications
    var SALT = "ddhV32bf"
    var TOKEN = "e2733fb35892d0a7197e534761549a9a"
    var USER = "magenta"
    var PASSWORD_HEX = ""

    // server address
    var BASE_URL = "https://apellai.duckdns.org"

    var CLIENT = "apellai"
    var VERSION = "1.16.1"
    var FORMAT = "json"

    const val NOW_PLAYING_CHANNEL_ID = "com.magentagang.apellai.NOW_PLAYING"
    const val NOW_PLAYING_NOTIFICATION_ID = 0x45
    const val NOTIFICATION_LARGE_ICON_SIZE = 144
    const val USER_AGENT = "apellai"
    const val NETWORK_FAILURE = "com.magentalgang.apellai.NETWORK_FAILURE"
    const val SEEKBAR_UPDATE_INTERVAL = 100L

    const val ADD_TO_QUEUE_ACTION = "com.magentagang.apellai.ADD_TO_QUEUE_ACTION"

    var SHUFFLE_MODE = 0
    var REPEAT_MODE = 0
}