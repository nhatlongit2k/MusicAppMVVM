package com.example.musicapp.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.app.ActivityManager
import android.content.*
import android.os.*
import android.view.View
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.musicapp.*
import com.example.musicapp.service.MyService
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var rlBottom: RelativeLayout
    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var imgPreviousSong: ImageView
    lateinit var imgNextSong: ImageView
    lateinit var imgPlayOrPause: ImageView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var btSearch: ImageView
    lateinit var edtSearch: EditText


    var isPlaying : Boolean?= false

    companion object{
        val MY_PERMISSION_REQUEST = 1
        var active = false
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var song: Song = p1?.getSerializableExtra("song_name") as Song
            var listSongPlaying: ArrayList<Song> = p1?.getSerializableExtra("list_song") as ArrayList<Song>
            var position: Int = p1.getIntExtra("position", 0)
            isPlaying = p1?.getBooleanExtra("status_player", false)
            var duration: Int = p1.getIntExtra("music_duration", 0)
            var action = p1?.getIntExtra("action_music", 0)
            if(action == MyService.ACTION_GET_MUSIC_FOR_MAIN){
                rlBottom.visibility = View.VISIBLE
                tvSongName.setText(listSongPlaying[position].title)
                tvArtist.setText(listSongPlaying[position].artist)
                if(isPlaying == true){
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
                }else{
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
            if(action == MyService.ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN){
                var intent: Intent = Intent(this@MainActivity, PlayMusicActivity::class.java)
                intent.putExtra("how_to_start", "just_open")
                intent.putExtra("position", position)
                intent.putExtra("list_song1", listSongPlaying)
                intent.putExtra("music_duration", duration)
                intent.putExtra("status_player", isPlaying)
                Log.d("TAG", "day: ")
                startActivityForResult(intent, 1)
            }
            if(action == MyService.ACTION_STOP){
                rlBottom.visibility = View.GONE
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            } else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }
        else{
            active = true
            initView()

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("send_action_to_main_activity"))
            if(isMyServiceRunning(MyService::class.java)){
                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
            }
        }
    }

    private fun initView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)

        rlBottom = findViewById(R.id.main_layout_bottom)
        tvSongName = findViewById(R.id.main_tv_song_title)
        tvArtist = findViewById(R.id.main_tv_song_author)
        imgNextSong = findViewById(R.id.main_bt_next_song)
        imgPreviousSong = findViewById(R.id.main_bt_previous_song)
        imgPlayOrPause = findViewById(R.id.main_bt_play_pause)
        btSearch = findViewById(R.id.bt_find_song)
        edtSearch = findViewById(R.id.edt_find_song)

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
        btSearch.setOnClickListener {
            var intent: Intent = Intent(this, SearchMusicActivity::class.java)
            intent.putExtra("edt_search", edtSearch.text.toString())
            startActivity(intent)
        }

        rlBottom.setOnClickListener {
            sendActionToService(MyService.ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSION_REQUEST -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
//                        doStuff()
                        initView()
                    }
                } else{
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun sendActionToService(action: Int){
        var intent: Intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        startService(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 111){
            if(isMyServiceRunning(MyService::class.java)){
                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        active = false
        super.onDestroy()
    }
}