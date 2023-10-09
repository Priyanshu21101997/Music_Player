package com.example.musicplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity(), IPlayList {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var playlistAdapter: PlaylistAdapter
    private var playListStateSubject = PublishSubject.create<PlayListState>()

    companion object{
        var playListStatic = MusicPlayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        setContentView(R.layout.activity_playlist)

        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerPlaylist
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val recyclerAdapter = PlaylistAdapter(this, MusicPlayList().ref)
        recyclerView.adapter = recyclerAdapter
        playlistAdapter = recyclerAdapter

        playlistAdapter.setListener(this)

        binding.addPlaylist.setOnClickListener{
            customAlertDialog()
        }

        playListStateSubject.observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({ it?.let  {
                       if(it == PlayListState.VISIBLE){
                           Log.d("RE_LIFE", "Visible hai bhai ")
                           binding.playlistEmpty.visibility = View.GONE
                           binding.recyclerPlaylist.visibility = View.VISIBLE
                       }
                else if( it == PlayListState.INVISIBLE){
                           Log.d("RE_LIFE", "onCreate: Here in invisible ")
                           binding.playlistEmpty.visibility = View.VISIBLE
                           binding.recyclerPlaylist.visibility = View.GONE
                       }
                else{
                           Log.d("RE_LIFE", "onCreate: else ")

                       }
            }
            },{
                Log.d("RE_LIFE", "Dikkat hai ${it} ")
            }).also {
                Log.d("RE_LIFE", "Dispose ")
            }
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add"){ dialog, _ ->
                val playListName = binder.playlistName.text?.trimEnd()?.trimStart()
                val personName = binder.yourName.text
                if(!playListName.isNullOrEmpty() && !personName.isNullOrEmpty()){
                    val isPresent = checkIfPlayListExist(playListName.toString())
                    if(isPresent){
                        Toast.makeText(this,"Playlist Already Exists !!", Toast.LENGTH_LONG).show()
                    }
                    else {
                        val playList = Playlist()
                        playList.name = playListName.toString()

                        playList.createdBy = personName.toString()
                        val calendar = Calendar.getInstance().time
                        val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
                        playList.createdDate = sdf.format(calendar)
                        playList.playList = ArrayList()
                        playListStatic.ref.add(playList)
                        Log.d("RE_LIFE", "customAlertDialog: ${playListStatic.ref.size}")
                        playlistAdapter.refreshPlayList(playListStatic.ref)
                    }
                }
                else{
                    if(playListName.isNullOrEmpty()){
                        Toast.makeText(this,"Playlist Name is required !!", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this,"Your Name is required !!", Toast.LENGTH_LONG).show()

                    }
                }
                managePlayListState()

                dialog.dismiss()
            }.show()
    }

    private fun checkIfPlayListExist(playListName: String): Boolean {
        val isPresent = playListStatic.ref.find { it.name == playListName }
        isPresent?.let { return true } ?:  return false
    }

    override fun onDeleteButtonClicked(position: Int) {
        playListStatic.ref.removeAt(position)
        playlistAdapter.refreshPlayList(playListStatic.ref)
        managePlayListState()

    }

    override fun onPlaylistItemClicked(position: Int) {
        val intent = Intent(this, PlaylistDetailsActivity::class.java)
        intent.putExtra("playlistindex",position)
        startActivity(intent)
    }

    private fun managePlayListState(){
        if(playListStatic.ref.size == 0){
            playListStateSubject.onNext(PlayListState.INVISIBLE)
        }
        else{
            playListStateSubject.onNext(PlayListState.VISIBLE)
        }
    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.refreshPlayList(playListStatic.ref)
        managePlayListState()
    }

    override fun onDestroy() {
        super.onDestroy()
//        compositeDisposable.dispose()
    }
}



enum class PlayListState{
    VISIBLE,
    INVISIBLE
}