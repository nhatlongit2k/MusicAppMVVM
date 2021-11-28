package com.example.musicapp.repository

import android.app.Application
import com.example.musicapp.api.RelatedRetrofitHelper

class RankingRepository(application: Application) {

    suspend fun getRinkingData() = RelatedRetrofitHelper.musicAPI.getRinkingData()
}