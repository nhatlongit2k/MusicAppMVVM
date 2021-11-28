package com.example.musicapp.databasemvvm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicapp.databasemvvm.dao.SongDao
import com.example.musicapp.model.Song

@Database(entities = [Song::class], version = 1)
abstract class SongDatabase : RoomDatabase(){
    abstract fun getSongDao(): SongDao

    companion object{
        @Volatile
        private var instance: SongDatabase? = null

        fun getInstance(context: Context): SongDatabase{
            if(instance == null){
                instance = Room.databaseBuilder(context, SongDatabase::class.java, "SongDatabase").build()
            }
            return instance!!
        }
    }
}