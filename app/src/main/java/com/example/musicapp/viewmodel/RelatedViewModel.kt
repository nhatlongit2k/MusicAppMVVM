package com.example.musicapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.musicapp.repository.RelatedRepository
import com.example.mvvm_retrofit.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.IllegalArgumentException

class RelatedViewModel(application: Application): ViewModel() {

    private val relatedRepository: RelatedRepository = RelatedRepository(application)

    fun getQuotesFromApi(type: String, id: String) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(relatedRepository.getRelatedMusic(type, id)))
        }catch (e: Exception){
            emit(Resource.error(null, e.message ?: "Error"))
        }
    }

    class RelatedViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(RelatedViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return RelatedViewModel(application) as T
            }
            throw IllegalArgumentException("Unable construce viewmodel")
        }
    }
}