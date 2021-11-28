package com.example.musicapp.repository

import android.app.Application
import com.example.musicapp.api.SearchRetrofitHelper

class SearchRepository(app: Application) {
    suspend fun getSearchSong(type: String, num:String, query:String) = SearchRetrofitHelper.musicAPI.getSeachSong(type, num, query)
}