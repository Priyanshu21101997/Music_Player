package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.SingleSongViewBinding

class MusicAdapter(private val context: Context,private var musicList:ArrayList<Music>):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    class ViewHolder(binding: SingleSongViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.songName
        val website = binding.songWebsite
        val image = binding.imageView
        val songTime = binding.songTime
        val root = binding.root
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        return ViewHolder(SingleSongViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.website.text = musicList[position].album
        holder.songTime.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
            when(MainActivity.search) {
                true -> sendIntent(position, "MusicAdpaterSearch")
                else -> sendIntent(position, "MusicAdapter")
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(position: Int, classNames: String) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra(index,position)
        intent.putExtra(className,classNames)
        context.startActivity(intent)
    }

}