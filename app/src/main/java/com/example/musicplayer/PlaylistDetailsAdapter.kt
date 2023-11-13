package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.SingleSongViewBinding

class PlaylistDetailsAdapter(private val context: Context, private var resultantMusicList: ArrayList<Music> )
    : RecyclerView.Adapter<PlaylistDetailsAdapter.ViewHolder>() {

    class ViewHolder(binding: SingleSongViewBinding ) :
        RecyclerView.ViewHolder(binding.root) {

        val title = binding.songName
        val website = binding.songWebsite
        val image = binding.imageView
        val songTime = binding.songTime
        val root = binding.root
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            SingleSongViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = resultantMusicList[position].title
        holder.website.text = resultantMusicList[position].album
        holder.songTime.text = formatDuration(resultantMusicList[position].duration)
        Glide.with(context)
            .load(resultantMusicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

    }

    override fun getItemCount():Int  = resultantMusicList.size

    fun deleteAllSongs(){
        resultantMusicList = arrayListOf()
        notifyDataSetChanged()
    }

    fun updateSongs(playList: ArrayList<Music>) {
        resultantMusicList = arrayListOf()
        resultantMusicList.addAll(playList)
        notifyDataSetChanged()
    }

}



