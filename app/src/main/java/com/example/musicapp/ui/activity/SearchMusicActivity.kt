package com.example.musicapp.ui.activity

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.service.MyService
import com.example.musicapp.R
import com.example.musicapp.Song
import com.example.musicapp.adapter.AdapterListSongOnl
import com.example.musicapp.model.model_search.Data
import com.example.musicapp.viewmodel.SearchViewModel
import com.example.musicapp.viewmodel.SongViewModel
import com.example.mvvm_retrofit.Status

const val SEARCH_URL = "http://ac.mp3.zing.vn/"
class SearchMusicActivity : AppCompatActivity() {

    var listSong: ArrayList<Song> = ArrayList()

    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var rlBottom: RelativeLayout
    lateinit var imgPreviousSong: ImageView
    lateinit var imgNextSong: ImageView
    lateinit var imgPlayOrPause: ImageView
    lateinit var imgBack: ImageView
    var isPlaying : Boolean?= false

    lateinit var data: Data
    lateinit var songRecyclerView: RecyclerView
    lateinit var layoutManager : LinearLayoutManager

    lateinit var pbLoading: ProgressBar

    private val searchViewModel: SearchViewModel by lazy{
        ViewModelProvider(this, SearchViewModel.SearchViewModelFactory(this.application)).get(SearchViewModel::class.java)
    }
    private val songViewModel: SongViewModel by lazy {
        ViewModelProvider(this, SongViewModel.SongViewModelFactory(this.application)).get(
            SongViewModel::class.java)
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var song: Song = p1?.getSerializableExtra("song_name") as Song
            isPlaying = p1?.getBooleanExtra("status_player", false)
            var action = p1?.getIntExtra("action_music", 0)
            if(action == MyService.ACTION_GET_MUSIC_FOR_MAIN){
                rlBottom.visibility = View.VISIBLE
                tvSongName.setText(song.title)
                tvArtist.setText(song.artist)
                if(isPlaying == true){
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
                }else{
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_music)

        initView()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("send_action_to_main_activity"))
        if(isMyServiceRunning(MyService::class.java)){
            sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
        }

        songRecyclerView = findViewById(R.id.recycler_song_list_search)
        layoutManager = LinearLayoutManager(this)
        songRecyclerView.layoutManager = layoutManager

        pbLoading = findViewById(R.id.pb_search_loading)
        pbLoading.visibility = View.VISIBLE
        var searchString = intent.getStringExtra("edt_search")
        if(searchString!=null){
            getData(searchString)
        }
    }

    private fun initView() {
        tvSongName = findViewById(R.id.search_tv_song_title)
        tvArtist = findViewById(R.id.search_tv_song_author)
        imgNextSong = findViewById(R.id.search_bt_next_song)
        imgPreviousSong = findViewById(R.id.search_bt_previous_song)
        imgPlayOrPause = findViewById(R.id.search_bt_play_pause)
        rlBottom = findViewById(R.id.search_layout_bottom)
        imgBack = findViewById(R.id.bt_Search_back)

        rlBottom.visibility = View.GONE

        imgNextSong.setOnClickListener {
            sendActionToService(MyService.ACTION_NEXT_MUSIC_FROM_MAIN)
        }
        imgPreviousSong.setOnClickListener {
            sendActionToService(MyService.ACTION_PREVIOUS_MUSIC_FROM_MAIN)
        }
        imgPlayOrPause.setOnClickListener {
            if(isPlaying == true){
                sendActionToService(MyService.ACTION_PAUSE_MUSIC_FROM_MAIN)
            }else{
                sendActionToService(MyService.ACTION_RESUME_MUSIC_FROM_MAIN)
            }
        }
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendActionToService(action: Int){
        var intent: Intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        startService(intent)
    }

    private fun getData(searchString: String) {
        searchViewModel.getSearchSongFromApi(searchString).observe(this, Observer {
            it.let { resource ->
                when(resource.status){
                    Status.SUCCESS->{
                        pbLoading.visibility = View.GONE
                        resource.data?.let { searchData->
                            for(i in 0..searchData.body()?.data!!.get(0).listSong.size-1){
                            val resource: String = "http://api.mp3.zing.vn/api/streaming/audio/${searchData.body()?.data!!.get(0).listSong[i].id}/128"
                            val image: String = "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/${searchData.body()?.data!!.get(0).listSong[i].image}"
                            listSong.add(Song(searchData.body()?.data!!.get(0).listSong[i].id, searchData.body()?.data!!.get(0).listSong[i].title, searchData.body()?.data!!.get(0).listSong[i].artist, resource, image))
                            }
                            songRecyclerView.adapter = AdapterListSongOnl(this, listSong, songViewModel, this)
                        }
                    }
                    Status.ERROR ->{
                        pbLoading.visibility = View.GONE
                        Log.d("TAG", "onCreate: ${it.message}")
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING->{
                        pbLoading.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 111){
            if(isMyServiceRunning(MyService::class.java)){
                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}