package com.example.musicapp.adapter

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.Song
import com.example.musicapp.viewmodel.SongViewModel
import kotlin.collections.ArrayList

class AdapterListSongOnline(val context: FragmentActivity?, val songList: ArrayList<Song>, val songViewModel: SongViewModel, val lifecycleOwner: LifecycleOwner): BaseAdapter(){

    var listSongDatabase: ArrayList<com.example.musicapp.model.Song> = ArrayList<com.example.musicapp.model.Song>()
    class ViewHolder(row: View){
        var imgSong: ImageView
        var tvTitle: TextView
        var tvAuthor: TextView
        var imgFavorite: ImageView
        var imgDownload: ImageView
        init {
            imgSong = row.findViewById(R.id.img_item_song_online)
            tvTitle = row.findViewById(R.id.tv_title_song_online)
            tvAuthor = row.findViewById(R.id.tv_author_song_online)
            imgFavorite = row.findViewById(R.id.img_favorite_song_online)
            imgDownload = row.findViewById(R.id.img_download_song_online)
        }
    }

    override fun getCount(): Int {
        return songList.size
    }

    override fun getItem(position: Int): Any {
        return songList.get(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var view : View?
        var viewHolder: ViewHolder
        if(convertView == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.item_song_online, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var song: Song = getItem(position) as Song
        viewHolder.tvTitle.text = song.title
        viewHolder.tvAuthor.text = song.artist
//        DownloadImageFromInternet(viewHolder.imgSong).execute(song.image)
        if (context != null) {
            Glide.with(context).load(song.image).into(viewHolder.imgSong)
        }
        songViewModel.getAllSongFromDatabase().observe(lifecycleOwner, Observer {
            listSongDatabase.clear()
            listSongDatabase.addAll(it)
            if(isInFavorite(song)){
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        })
        viewHolder.imgFavorite.setOnClickListener {
            if(isInFavorite(song)){
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                val songToData = com.example.musicapp.model.Song(
                    song.id!!,
                    song.title,
                    song.artist!!,
                    song.resource,
                    song.image!!
                )
                songViewModel.deleteSong(songToData)
            }else{
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                val songToData = com.example.musicapp.model.Song(
                    song.id!!,
                    song.title,
                    song.artist!!,
                    song.resource,
                    song.image!!
                )
                songViewModel.insertSong(songToData)
            }
        }

        viewHolder.imgDownload.setOnClickListener {
            stratDownloadFile(song)
        }

        return view as View
    }

    private fun stratDownloadFile(song: Song) {
        Log.d("TAG", "stratDownloadFile: ${song.resource}")
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(song.resource))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(song.title)
        request.setDescription("Download song...")

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, System.currentTimeMillis().toString())

        var downloadManager: DownloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if(downloadManager!=null){
            downloadManager.enqueue(request)
        }
    }

    fun isInFavorite(song: Song): Boolean {
        for(i in 0..listSongDatabase.size-1){
            if(listSongDatabase.get(i).id.equals(song.id)==true){
                return true
            }
        }
        return false
    }
}