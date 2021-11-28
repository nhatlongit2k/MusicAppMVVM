package com.example.musicapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FavoriteSong")
class Song(
    @PrimaryKey() @ColumnInfo(name = "Id") var id: String = "",
    @ColumnInfo(name = "Title")var title:String = "",
    @ColumnInfo(name = "Artist")var artist: String ="",
    @ColumnInfo(name = "Resource")var resource: String ="",
    @ColumnInfo(name = "Image")var image: String =""
): Serializable {
}