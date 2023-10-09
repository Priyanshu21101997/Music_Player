package com.example.musicplayer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.SingleSongEditViewBinding

class AddSongsToPlaylistAdapter(private val context: Context, private var resultantMusicList: ArrayList<Music> )
    : RecyclerView.Adapter<AddSongsToPlaylistAdapter.ViewHolder>() {

    var addToPlaylistListener: IAddToPlaylist? = null

    class ViewHolder(binding: SingleSongEditViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.songName
        val website = binding.songWebsite
        val image = binding.imageView
        val checkbox = binding.checkbox
        val root = binding.root
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddSongsToPlaylistAdapter.ViewHolder {



        val view = AddSongsToPlaylistAdapter.ViewHolder(
            SingleSongEditViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        return view
    }

    override fun onBindViewHolder(holder: AddSongsToPlaylistAdapter.ViewHolder, position: Int) {
        holder.title.text = resultantMusicList[position].title
        holder.website.text = resultantMusicList[position].album
        Glide.with(context)
            .load(resultantMusicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
            addToPlaylistListener?.onSongLongPressed(position)
            notifyDataSetChanged()
        }


        if(resultantMusicList[position].isChecked){
            holder.checkbox.setImageResource(R.drawable.ic_check)
        }
        else{
            holder.checkbox.setImageResource(R.drawable.ic_unchecked)
        }
    }

    override fun getItemCount(): Int {
        return resultantMusicList.size
    }

    fun setListener(addSongsToPlaylistActivity: AddSongsToPlaylistActivity) {
        this.addToPlaylistListener = addSongsToPlaylistActivity
    }

    fun updateResultantPlaylist(resultantPlayList: List<Music>) {
        resultantMusicList = arrayListOf()
        resultantMusicList.addAll(resultantPlayList as ArrayList<Music>)
        notifyDataSetChanged()
    }
}