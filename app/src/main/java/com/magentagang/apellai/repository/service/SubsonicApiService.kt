package com.magentagang.apellai.repository.service

import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.magentagang.apellai.model.Constants
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


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .build()

interface SubsonicApiService {

    // work to do, not tested
    @GET("rest/getAlbumList2")
    fun getAlbumListAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,
        // Type choice (required)
        @Query("type") type: String = Constants.DEFAULT_ALBUM_TYPE,
        // Setting Default values
        @Query("offset") offset: Int = 0,
        @Query("size") size: Int = 20,
        //TODO(fromYear, toYear implementation)
        //@Query("fromYear") fromYear : Int = ???
        //@Query("toYear") toYear : Int = ???
        @Query("genre") genre: String = "",
        @Query("musicFolderId") musicFolderId: String = "",
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/ping")
    fun getPingAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/getAlbum")
    fun getAlbumAsync(
        // Authentications
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String = Constants.ALBUM_ID
    ): Deferred<SubsonicResponseRoot>


    @GET("rest/getSong")
    fun getTrackAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String = Constants.SONG_ID
    ): Deferred<SubsonicResponseRoot>


    //tested
    @GET("rest/getArtist")
    fun getArtistAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String = Constants.ARTIST_ID
    ): Deferred<SubsonicResponseRoot>

    //tested
    @GET("rest/getArtists")
    fun getArtistsAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("musicFolderId") musicFolderId: String = "" // not applicable
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/star")
    fun starSongAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String,
    ): Deferred<SubsonicResponseRoot>


    @GET("rest/star")
    fun starAlbumAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("albumId") albumId: String,
    ): Deferred<SubsonicResponseRoot>


    @GET("rest/star")
    fun starArtistAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("artistId") artistId: String,
    ): Deferred<SubsonicResponseRoot>

    @GET("rest/unstar")
    fun unstarSongAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String,
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/unstar")
    fun unstarAlbumAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("albumId") albumId: String,
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/unstar")
    fun unstarArtistAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("artistId") artistId: String,
    ): Deferred<SubsonicResponseRoot>

    // tested
    @GET("rest/getStarred2")
    fun getStarred2Async(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("musicFolderId") musicFolderId: String = ""
    ): Deferred<SubsonicResponseRoot>


    //TODO(ADD Other params and what what what)
    @GET("rest/stream")
    fun getStreamBinaryAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String,
    ): Deferred<AssetFileDescriptor>


    // tested
    @GET("rest/search3")
    fun search3Async(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int = 20,
        @Query("artistOffset") artistOffset: Int = 0,
        @Query("albumCount") albumCount: Int = 20,
        @Query("albumOffset") albumOffset: Int = 0,
        @Query("songCount") songCount: Int = 20,
        @Query("songOffset") songOffset: Int = 0,
        @Query("musicFolderId") musicFolderId: String = "",
    ): Deferred<SubsonicResponseRoot>

    @GET("rest/getCoverArt")
    fun getCoverArtAsync(
        // Authentications
        @Query("s") salt: String = Constants.SALT,
        @Query("t") token: String = Constants.TOKEN,
        // Required Params
        @Query("c") client: String = Constants.CLIENT,
        @Query("u") user: String = Constants.USER,
        @Query("v") version: String = Constants.VERSION,
        // Format choice
        @Query("f") format: String = Constants.FORMAT,

        @Query("id") id: String,
        @Query("size") size: Int? = null,
    ): Deferred<Bitmap>
}


object SubsonicApi {
    val retrofitService: SubsonicApiService by lazy {
        retrofit.create(SubsonicApiService::class.java)
    }
}