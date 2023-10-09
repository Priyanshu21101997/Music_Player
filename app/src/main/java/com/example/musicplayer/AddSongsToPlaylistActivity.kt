package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityAddSongsToPlaylistBinding

class AddSongsToPlaylistActivity : AppCompatActivity(),IAddToPlaylist {

    private lateinit var binding: ActivityAddSongsToPlaylistBinding
    private lateinit var addSongsToPlaylistAdapter: AddSongsToPlaylistAdapter
    private lateinit var resultantPlayList: List<Music>
    var selectedSongs = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        setContentView(R.layout.activity_add_songs_to_playlist)
        binding = ActivityAddSongsToPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val index = intent.getIntExtra("index",0)


        val recyclerView = binding.recyclerAddToPlaylist
        recyclerView.layoutManager = LinearLayoutManager(this)
        resultantPlayList = MainActivity.musicListMA.minus(PlaylistActivity.playListStatic.ref[index].playList)
        val recyclerAdapter = AddSongsToPlaylistAdapter(this, resultantPlayList as ArrayList<Music>)
        recyclerView.adapter = recyclerAdapter
        addSongsToPlaylistAdapter = recyclerAdapter

        addSongsToPlaylistAdapter.setListener(this)

        binding.addSelected.setOnClickListener {
            val selectedSongsMusic = fetchSelectedSongsFromIndex(selectedSongs)
            PlaylistActivity.playListStatic.ref[index].playList.addAll(selectedSongsMusic)
            selectedSongs = arrayListOf()
            resultantPlayList = arrayListOf()
            resultantPlayList = MainActivity.musicListMA.minus(PlaylistActivity.playListStatic.ref[index].playList)
            addSongsToPlaylistAdapter.updateResultantPlaylist(resultantPlayList)
            binding.addSelected.visibility = View.GONE
            finish()
        }
    }

    override fun onSongLongPressed(position: Int) {
        Log.d("RE_LIFE", "onSongLongPressed: $position")
        resultantPlayList[position].isChecked = !resultantPlayList[position].isChecked
        val index = selectedSongs.indexOf(position)
        if(resultantPlayList[position].isChecked && index == -1){
            selectedSongs.add(position)
        }
        else if(!resultantPlayList[position].isChecked && index!=-1){
            selectedSongs.removeAt(index)
        }

        Log.d("RE_LIFE", "onSongLongPressed: ${selectedSongs.size} ")

        if(selectedSongs.size>0){
            binding.addSelected.visibility = View.VISIBLE
        }
        else{
            binding.addSelected.visibility = View.GONE
        }
    }

    fun fetchSelectedSongsFromIndex(selectedSongs: ArrayList<Int>): ArrayList<Music> {
        val selectedSongsMusic = arrayListOf<Music>()
        selectedSongs.forEach{
            resultantPlayList[it].isChecked = false
            selectedSongsMusic.add(resultantPlayList[it])
        }
        return selectedSongsMusic
    }

}