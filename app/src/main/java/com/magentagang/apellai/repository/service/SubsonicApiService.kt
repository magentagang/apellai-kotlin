package com.magentagang.apellai.repository.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.magentagang.apellai.model.SubsonicResponseRoot
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


// test variables
private const val ALBUM_ID = "f76fcdde71a3708aa45de4fc841773aa"
private const val SONG_ID = "f408df38cb3ca7f472d18f6b1d64f8dc"
private const val ALBUM_TYPE = "newest"

// Authentications
private const val SALT = "??"
private const val TOKEN = "??"
private const val USER = "??"
// server address
private const val BASE_URL = "??"


private const val CLIENT = "apellai"
private const val VERSION = "1.16.1"
private const val FORMAT = "json"

// INFO TO BE DELETED LATER


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java,  Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface SubsonicApiService {
    @GET("rest/getAlbumList2")
    fun getAlbumListAsync(
        // Authentications
        @Query("s") salt: String = SALT,
        @Query("t") token: String = TOKEN,
        // Required Params
        @Query("c") client: String = CLIENT,
        @Query("u") user: String = USER,
        @Query("v") version: String = VERSION,
        // Format choice
        @Query("f") format: String = FORMAT,
        // Type choice (required)
        @Query("type") type: String = ALBUM_TYPE,
        // Setting Default values
        @Query("offset") offset: Int = 0,
        @Query("size") size: Int = 20
    ): Deferred<SubsonicResponseRoot>

    @GET("rest/ping")
    fun getPingAsync(
        // Authentications
        @Query("s") salt: String = SALT,
        @Query("t") token: String = TOKEN,
        // Required Params
        @Query("c") client: String = CLIENT,
        @Query("u") user: String = USER,
        @Query("v") version: String = VERSION,
        // Format choice
        @Query("f") format: String = FORMAT,
    ): Deferred<SubsonicResponseRoot>

    @GET("rest/getAlbum")
    fun getAlbumAsync(
        // Authentications
        // Authentications
        @Query("s") salt: String = SALT,
        @Query("t") token: String = TOKEN,
        // Required Params
        @Query("c") client: String = CLIENT,
        @Query("u") user: String = USER,
        @Query("v") version: String = VERSION,
        // Format choice
        @Query("f") format: String = FORMAT,
        @Query("id") id: String = ALBUM_ID
    ): Deferred<SubsonicResponseRoot>

    @GET("rest/getSong")
    fun getSongAsync(
        // Authentications
        @Query("s") salt: String = SALT,
        @Query("t") token: String = TOKEN,
        // Required Params
        @Query("c") client: String = CLIENT,
        @Query("u") user: String = USER,
        @Query("v") version: String = VERSION,
        // Format choice
        @Query("f") format: String = FORMAT,

        @Query("id") id: String = SONG_ID
    ): Deferred<SubsonicResponseRoot>
}


object SubsonicApi {
    val retrofitService: SubsonicApiService by lazy {
        retrofit.create(SubsonicApiService::class.java)
    }
}