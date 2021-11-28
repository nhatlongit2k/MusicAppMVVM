package com.example.musicapp.adapter

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.ui.activity.PlayMusicActivity
import com.example.musicapp.R
import com.example.musicapp.Song
import com.example.musicapp.viewmodel.SongViewModel

class AdapterListSongOnl(val context: Activity?, val listSong: ArrayList<Song>, val songViewModel: SongViewModel, val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<AdapterListSongOnl.MyViewHolder>() {

    var listSongDatabase: ArrayList<com.example.musicapp.model.Song> = ArrayList<com.example.musicapp.model.Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song_online, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listSong[position]

        songViewModel.getAllSongFromDatabase().observe(lifecycleOwner, Observer {
            listSongDatabase.clear()
            listSongDatabase.addAll(it)
            if(isInFavorite(currentItem)){
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        })

        holder.tvTitle.text = currentItem.title
        holder.tvAuthor.text = currentItem.artist
//        DownloadImageFromInternet(holder.imgSong).execute(currentItem.image)

        if (context != null) {
            Glide.with(context).load(currentItem.image).into(holder.imgSong)
        }
        holder.imgFavorite.setOnClickListener {
            if(isInFavorite(currentItem)){
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                val song = com.example.musicapp.model.Song(
                    currentItem.id!!,
                    currentItem.title,
                    currentItem.artist!!,
                    currentItem.resource,
                    currentItem.image!!
                )
                songViewModel.deleteSong(song)
            }else{
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                val song = com.example.musicapp.model.Song(
                    currentItem.id!!,
                    currentItem.title,
                    currentItem.artist!!,
                    currentItem.resource,
                    currentItem.image!!
                )
                songViewModel.insertSong(song)
            }
        }
        holder.imgDownload.setOnClickListener {
            stratDownloadFile(currentItem)
        }
        holder.itemView.setOnClickListener {
            var intent: Intent = Intent(context, PlayMusicActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list_song1", listSong)
            context?.startActivityForResult(intent, 1)
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imgSong: ImageView = itemView.findViewById(R.id.img_item_song_online)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title_song_online)
        var tvAuthor: TextView = itemView.findViewById(R.id.tv_author_song_online)
        var imgFavorite: ImageView = itemView.findViewById(R.id.img_favorite_song_online)
        var imgDownload: ImageView = itemView.findViewById(R.id.img_download_song_online)
    }
    fun isInFavorite(song: Song): Boolean {
        for(i in 0..listSongDatabase.size-1){
            if(listSongDatabase.get(i).id.equals(song.id)==true){
                return true
            }
        }
        return false
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
}