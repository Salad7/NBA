package com.salad.nba.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NBAApi {
    @GET("players")
    suspend fun getPlayers() : String

    @GET("players")
    suspend fun searchPlayer(@Query("search") search :String) : String
}