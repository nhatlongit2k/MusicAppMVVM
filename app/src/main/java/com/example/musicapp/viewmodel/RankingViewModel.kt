package com.example.musicapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.musicapp.repository.RankingRepository
import com.example.mvvm_retrofit.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.IllegalArgumentException

class RankingViewModel(application: Application): ViewModel() {
    private val rankingRepository: RankingRepository = RankingRepository(application)

    fun getRankingData() = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(rankingRepository.getRinkingData()))
        }catch (e: Exception){
            emit(Resource.error(null, e.message ?: "Error"))
        }
    }

    class RankingViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(RankingViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return RankingViewModel(application) as T
            }
            throw IllegalArgumentException("Unable construce viewmodel")
        }
    }
}