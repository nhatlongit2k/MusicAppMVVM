package com.example.musicapp.model.model_ranking

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataArraySong(
    @SerializedName("song")
    @Expose
    var songCode: ArrayList<SongCode>
) {
}