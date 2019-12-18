package com.example.mcservermonitor.mcapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {
    @GET("user/profiles/{uid}/names")
    fun getPlayerResponseByUID(@Path("uid") uid: String): Call<Array<PlayerResponse>>
}