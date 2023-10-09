package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var playlistDetailsAdapter: PlaylistDetailsAdapter
    private var index = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)

        setContentView(R.layout.activity_playlist_details)

        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        index = intent.getIntExtra("playlistindex",0)

        binding.playlistName.text = PlaylistActivity.playListStatic.ref[index].name
        binding.personName.text = PlaylistActivity.playListStatic.ref[index].createdBy
        binding.onDate.text = PlaylistActivity.playListStatic.ref[index].createdDate


        binding.addBtnPlaylistDetails.setOnClickListener {
            val intent = Intent(this, AddSongsToPlaylistActivity::class.java)
            intent.putExtra("index",index)
            startActivity(intent)
        }

        binding.deleteAllPD.setOnClickListener {
            PlaylistActivity.playListStatic.ref[index].playList = arrayListOf()
            playlistDetailsAdapter.deleteAllSongs()
            setPlaylistDetails()
        }

        val recyclerView = binding.recyclerPlaylistDetail
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = PlaylistDetailsAdapter(this, PlaylistActivity.playListStatic.ref[index].playList)
        recyclerView.adapter = recyclerAdapter
        playlistDetailsAdapter = recyclerAdapter
    }

    private fun runtimeString(playList: ArrayList<Music>): String {
        var runtime = 0L
        playList.forEach {
            runtime+= it.duration
        }

        return runtime.toString()
    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistDetailsAdapter.updateSongs(PlaylistActivity.playListStatic.ref[index].playList)
        setPlaylistDetails()
    }


    @SuppressLint("SetTextI18n")
    private fun setPlaylistDetails(){
        binding.detailsPlaylist.text = PlaylistActivity.playListStatic.ref[index].playList.size.toString() + " and " + runtimeString(PlaylistActivity.playListStatic.ref[index].playList)
        if(PlaylistActivity.playListStatic.ref[index].playList.size > 0) {
            Glide.with(this)
                .load(PlaylistActivity.playListStatic.ref[index].playList[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.playListCover)
        }else{
            Glide.with(this)
                .load(R.drawable.splash_screen)
                .into(binding.playListCover)
        }
    }
}

