package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityFavouriteBinding
import com.example.musicplayer.databinding.ActivityMainBinding

class FavouriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavouriteBinding
    private lateinit var favouritesAdapter: FavouritesAdapter

    companion object {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        setContentView(R.layout.activity_favourite)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val recyclerView = binding.recyclerFavourites
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        val recyclerAdapter = FavouritesAdapter(this, MainActivity.favouritesList)
        recyclerView.adapter = recyclerAdapter
        favouritesAdapter = recyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        favouritesAdapter.updateMusicListWithFavourite()
    }
}