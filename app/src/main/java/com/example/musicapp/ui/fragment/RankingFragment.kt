package com.example.musicapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.*
import com.example.musicapp.adapter.AdapterListSongOnl
import com.example.musicapp.model.model_ranking.Ranking
import com.example.musicapp.viewmodel.RankingViewModel
import com.example.musicapp.viewmodel.SongViewModel
import com.example.mvvm_retrofit.Status

class RankingFragment : Fragment() {

    var listSong: ArrayList<Song>? = ArrayList<Song>()
    lateinit var rankingList: Ranking
    lateinit var pbLoading: ProgressBar
    lateinit var songRecyclerView: RecyclerView
    lateinit var layoutManager : LinearLayoutManager


    private val rankingViewModel: RankingViewModel by lazy{
        ViewModelProvider(this, RankingViewModel.RankingViewModelFactory(requireActivity().application)).get(
            RankingViewModel::class.java)
    }
    private val songViewModel: SongViewModel by lazy {
        ViewModelProvider(this, SongViewModel.SongViewModelFactory(requireActivity().application)).get(SongViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

        songRecyclerView = view.findViewById(R.id.recycler_song_list)
        layoutManager = LinearLayoutManager(context)
        songRecyclerView.layoutManager = layoutManager
        pbLoading = view.findViewById(R.id.pb_loading)
        pbLoading.visibility = View.VISIBLE
        getData()

        return view
    }

    private fun getData() {
        rankingViewModel.getRankingData().observe(viewLifecycleOwner, Observer {
            it.let { resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        pbLoading.visibility = View.GONE
                        resource.data?.let { searchData->
                            val size = searchData.body()!!.dataArraySong.songCode.size-1
                            for(i in 0..size){
                                var resource = "http://api.mp3.zing.vn/api/streaming/audio/${searchData.body()!!.dataArraySong?.songCode.get(i).id}/128"
                                listSong?.add(Song(searchData.body()!!.dataArraySong?.songCode.get(i).id,
                                    searchData.body()!!.dataArraySong?.songCode.get(i).title,
                                    searchData.body()!!.dataArraySong?.songCode.get(i).artist,
                                    resource,
                                    searchData.body()!!.dataArraySong?.songCode.get(i).image))
                            }
                            pbLoading.visibility = View.GONE
                            songRecyclerView.adapter = AdapterListSongOnl(activity, listSong!!, songViewModel, viewLifecycleOwner)
                        }
                    }
                    Status.ERROR ->{
                        pbLoading.visibility = View.GONE
                        Log.d("TAG", "onCreate: ${it.message}")
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING->{
                        pbLoading.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}