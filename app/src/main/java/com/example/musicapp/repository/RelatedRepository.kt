package com.example.musicapp.repository

import android.app.Application
import com.example.musicapp.api.RelatedRetrofitHelper

class RelatedRepository(application: Application) {

    suspend fun getRelatedMusic(type: String, id: String) = RelatedRetrofitHelper.musicAPI.getRelatedMusic(type, id)
}