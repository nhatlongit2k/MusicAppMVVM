package com.example.musicapp.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicapp.databasemvvm.repository.SongRepository
import com.example.musicapp.model.Song
import kotlinx.coroutines.launch

class SongViewModel(application: Application): ViewModel() {
    private val songRepository: SongRepository = SongRepository(application)

    fun insertSong(song: Song) = viewModelScope.launch {
        songRepository.insertSong(song)
    }
    fun deleteSong(song: Song) = viewModelScope.launch {
        songRepository.deleteSong(song)
    }
    fun getAllSongFromDatabase(): LiveData<List<Song>> = songRepository.getAllSongFromDatabase()

    class SongViewModelFactory(private val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SongViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SongViewModel(application) as T
            }
            throw IllegalArgumentException("Unable construce viewmodel")
        }

    }
}