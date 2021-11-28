package com.example.musicapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.R
import com.example.musicapp.Song
import com.example.musicapp.adapter.AdapterListSongOnline
import com.example.musicapp.viewmodel.RelatedViewModel
import com.example.musicapp.viewmodel.SongViewModel
import com.example.mvvm_retrofit.Status

class RelatedMusicFragment : Fragment() {
    lateinit var playMusicActivity: PlayMusicActivity
    var listSong: ArrayList<Song> = ArrayList<Song>()
    lateinit var listView: ListView

    private val relatedViewModel: RelatedViewModel by lazy{
        ViewModelProvider(this, RelatedViewModel.RelatedViewModelFactory(requireActivity().application)).get(RelatedViewModel::class.java)
    }
    private val songViewModel: SongViewModel by lazy {
        ViewModelProvider(this, SongViewModel.SongViewModelFactory(requireActivity().application)).get(SongViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_related_music, container, false)
        playMusicActivity = activity as PlayMusicActivity
        listView = view.findViewById(R.id.listview_fragRelated_song)
        getData()
        return view
    }

    private fun getData() {
        var id: String? = playMusicActivity.listSong1[playMusicActivity.position].id
        if(id != null){
            relatedViewModel.getQuotesFromApi("audio", id.toString()).observe(viewLifecycleOwner, Observer {
                it.let { resource ->
                    when(resource.status){
                        Status.SUCCESS->{
//                            pbLoading.visibility = View.GONE
                            resource.data?.let { relatedData->
                                for(i in 0..relatedData.body()?.data!!.item.size-1){
                                    var resource = "http://api.mp3.zing.vn/api/streaming/audio/${relatedData.body()!!.data.item[i].id}/128"
                                    listSong?.add(Song(relatedData.body()!!.data.item[i].id,
                                        relatedData.body()!!.data.item[i].title,
                                        relatedData.body()!!.data.item[i].artist,
                                        resource,
                                        relatedData.body()!!.data.item[i].image))
                                }
                                listView.adapter = AdapterListSongOnline(activity, listSong!!, songViewModel, viewLifecycleOwner)
                                listView.setOnItemClickListener { adapterView, view, i, l ->
                                    playMusicActivity.setUpViewPager()
                                    playMusicActivity.listSong1 = listSong
                                    playMusicActivity.position = i
                                    playMusicActivity.stopService()
                                    playMusicActivity.startService()
                                }
                            }
                        }
                        Status.ERROR ->{
//                            pbLoading.visibility = View.GONE
                            Log.d("TAG", "onCreate: ${it.message}")
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING->{
//                            pbLoading.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }
    }
}