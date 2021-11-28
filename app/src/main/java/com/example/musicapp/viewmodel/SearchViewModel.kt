package com.example.musicapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.musicapp.repository.SearchRepository
import com.example.mvvm_retrofit.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.IllegalArgumentException

class SearchViewModel(application: Application): ViewModel() {
    private val searchRepository: SearchRepository = SearchRepository(application)

    fun getSearchSongFromApi(query: String) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(searchRepository.getSearchSong("artist,song,key,code", "500", query)))
        }catch (e: Exception){
            emit(Resource.error(null, e.message ?: "Error"))
        }
    }

    class SearchViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SearchViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(application) as T
            }
            throw IllegalArgumentException("Unable construce viewmodel")
        }
    }
}