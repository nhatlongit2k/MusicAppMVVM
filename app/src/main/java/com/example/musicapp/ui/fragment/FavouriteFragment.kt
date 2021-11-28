package com.example.musicapp.ui.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.*
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.adapter.AdapterListSongOnline
import com.example.musicapp.viewmodel.SongViewModel

class FavouriteFragment : Fragment() {

    var listSong: ArrayList<Song>? = ArrayList<Song>()
    lateinit var listView: ListView

    private val songViewModel: SongViewModel by lazy {
        ViewModelProvider(this, SongViewModel.SongViewModelFactory(requireActivity().application)).get(SongViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        listView = view.findViewById(R.id.listview_fragFavorite_song)

        songViewModel.getAllSongFromDatabase().observe(viewLifecycleOwner, Observer {
            listSong?.clear()
            for(i in 0..it.size-1){
                listSong?.add(Song(it[i].id, it[i].title, it[i].artist, it[i].resource, it[i].image))
            }
            listView.adapter = AdapterListSongOnline(activity, listSong!!, songViewModel, viewLifecycleOwner)
        })

        listView.setOnItemClickListener { adapterView, view, position, id ->
            var intent: Intent = Intent(activity, PlayMusicActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list_song1", listSong)
            intent.putExtra("song_name1", listSong!![position].title)
            startActivityForResult(intent, 1)
        }
        return view
    }
}