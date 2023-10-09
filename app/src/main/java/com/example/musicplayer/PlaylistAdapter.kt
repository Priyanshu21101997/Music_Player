package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter(private val context: Context, private var playList:ArrayList<Playlist>):
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    var playlistListener: IPlayList? = null

    class ViewHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val title = binding.textViewPA
        val image = binding.imageViewPA
        val deleteBtn = binding.deleteBtnPA
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PlaylistViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = playList[position].name
        Glide.with(context)
            .load(playList[position].playList.getOrNull(0).let{it?.artUri} ?: R.drawable.splash_screen )
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        holder.deleteBtn.setOnClickListener {
            val customDialog = LayoutInflater.from(context).inflate(R.layout.add_playlist_dialog, holder.root, false)
            val builder = MaterialAlertDialogBuilder(context)
            builder.setView(customDialog)
                .setTitle("Are you sure you want to delete playList ??")
                .setPositiveButton("Yes"){ dialog, _ ->
                    playlistListener?.onDeleteButtonClicked(position)
                    dialog.dismiss()}
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        holder.root.setOnClickListener {
            playlistListener?.onPlaylistItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return playList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlayList(_playList : ArrayList<Playlist>){
        playList = ArrayList()
        playList.addAll(_playList)
        notifyDataSetChanged()
    }

    fun setListener(playlistActivity: PlaylistActivity) {
        this.playlistListener = playlistActivity
    }

}