package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object{
        var musicListMA = ArrayList<Music>()
        var musicListSearch =  ArrayList<Music>()
        var search = false
        var NOWONFRAGMENT = "nowOnFragment"
        lateinit var sharedPreferences : SharedPreferences
        var favouritesList: ArrayList<Music> = arrayListOf()

    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicListMA = getAllAudio()
        search = false
        initialiseLayout()
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        binding.playlistsBtn.setOnClickListener {
            val intent = Intent(this, PlaylistActivity::class.java)
            startActivity(intent)
        }

        binding.favouritesBtn.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(index,0)
            intent.putExtra(className,"MainActivity")
            startActivity(intent)
        }

        binding.navDrawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_LONG).show()
                R.id.navSettings -> Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show()
                R.id.navAbout -> Toast.makeText(this, "About", Toast.LENGTH_LONG).show()
                R.id.navExit -> closeApplication()

            }
            true
        }
    }


    private fun requestPermissions(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Thankyou For Granting Permissions",Toast.LENGTH_LONG).show()
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initialiseLayout(){

                requestPermissions()

        setTheme(R.style.Theme_MusicPlayer)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Nav Drawer

        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Recycler View

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = MusicAdapter(this, musicListMA)
        recyclerView.adapter = recyclerAdapter
        musicAdapter = recyclerAdapter
        binding.totalSongs.text = "Total Songs : "+ recyclerAdapter.itemCount

        setNowPlayingFragment()

    }

    private fun getAllAudio():ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
            selection, null, MediaStore.Audio.Media.DATE_ADDED + " DESC" )

        while(cursor != null && cursor.moveToNext()){
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val uri = Uri.parse("content://media/external/audio/albumart")
            val artUri = Uri.withAppendedPath(uri,albumId).toString()
            val musicObj = Music(id = id, title = title, album = album, artist = artist,
                duration = duration, path = path, artUri = artUri)
            val file = File(path)
            if(file.exists()) // Sometimes the path doesnot have file ,m so check if there is file for that path.
                tempList.add(musicObj)
        }
        cursor?.close()
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        // User stopped and song and want to close Music Player
        if(!PlayerActivity.isMediaPlaying && PlayerActivity.musicService != null){
            PlayerActivity?.musicService?.stopForeground(true)
            PlayerActivity?.musicService?.mediaPlayer?.release()
            PlayerActivity.musicService = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if(newText != null){
                    var userInput = newText.lowercase()
                    for(song in musicListMA){
                        if(song.title.lowercase().contains(userInput)){
                            musicListSearch.add(song)
                        }
                    }
                    search = true
                    musicAdapter.updateMusicList(musicListSearch)
                }
                return true
            }


        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun setNowPlayingFragment(){
        val nowPlayingFragment = nowPlayingFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout,nowPlayingFragment, NOWONFRAGMENT)
            .addToBackStack(null)
            .commit();
    }
}