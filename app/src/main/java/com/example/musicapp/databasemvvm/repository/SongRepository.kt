package com.example.musicapp.databasemvvm.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.musicapp.databasemvvm.SongDatabase
import com.example.musicapp.databasemvvm.dao.SongDao
import com.example.musicapp.model.Song

class SongRepository(app: Application) {
    private val songDao: SongDao
    init {
        val songDatabase: SongDatabase = SongDatabase.getInstance(app)
        songDao = songDatabase.getSongDao()
    }

    suspend fun insertSong(song: Song) = songDao.insertSong(song)
    suspend fun deleteSong(song: Song) = songDao.deleteSong(song)

    fun getAllSongFromDatabase(): LiveData<List<Song>> = songDao.getAllSongFromDatabase()
}