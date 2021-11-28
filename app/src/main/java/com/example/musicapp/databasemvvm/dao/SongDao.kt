package com.example.musicapp.databasemvvm.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.musicapp.model.Song

@Dao
interface SongDao {
    @Insert
    suspend fun insertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("select * from FavoriteSong")
    fun getAllSongFromDatabase():LiveData<List<Song>>
}