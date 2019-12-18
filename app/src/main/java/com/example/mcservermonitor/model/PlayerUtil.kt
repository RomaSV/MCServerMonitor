package com.example.mcservermonitor.model

import android.util.Log
import com.example.mcservermonitor.mcapi.APIService
import com.example.mcservermonitor.mcapi.PlayerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.mojang.com"

fun getUserNamesByUIDS(ids: Array<String>): Array<String> {
    val result = mutableListOf<String>()
    var piratesCount = 0

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    val service = retrofit.create(APIService::class.java)

    for (uid in ids) {
        val c = service.getPlayerResponseByUID(uid.filter { c -> c != '-' })
        val response = c.execute()
        if (response.isSuccessful) {
            if (response.body() != null) {
                Log.i("MC", response.body()!![0].name)
                result.add(response.body()!![0].name)
            } else {
                // If the record is missing in the MC database
                // then the account is fake
                result.add("Pirate #${++piratesCount}")
            }
        } else {
            result.add("Error")
        }
    }
    return result.toTypedArray()
}
