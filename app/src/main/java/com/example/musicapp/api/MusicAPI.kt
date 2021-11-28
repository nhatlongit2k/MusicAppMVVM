package com.example.musicapp.api

import com.example.musicapp.model.model_ranking.Ranking
import com.example.musicapp.model.model_related.RelatedData
import com.example.musicapp.model.model_search.SearchData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicAPI {

    @GET("complete")
    suspend fun getSeachSong(@Query("type") type: String,
                          @Query("num") num: String,
                          @Query("query") query: String): Response<SearchData>

    @GET("xhr/recommend")
    suspend fun getRelatedMusic(@Query("type") type: String, @Query("id") id:String): Response<RelatedData>

    @GET("xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1")
    suspend fun getRinkingData(): Response<Ranking>
}