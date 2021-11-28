package com.example.musicapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchRetrofitHelper {

    private const val BASE_URL = "http://ac.mp3.zing.vn/"

    private val builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
    val retrofit = builder.build()
    val musicAPI: MusicAPI = retrofit.create(MusicAPI::class.java)
}