package com.example.musicplayer

import android.media.MediaMetadataRetriever
import android.util.Log
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String,
    var isFavourite: Boolean = false,
    var isChecked: Boolean = false
)

 class Playlist(){
    lateinit var name: String
    lateinit var playList: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdDate: String

}


class MusicPlayList{
    var ref : ArrayList<Playlist> = arrayListOf()
}

fun formatDuration(duration: Long):String{

    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun getImageArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

fun playNextSong(){
    if(!PlayerActivity.repeat) {
        if (PlayerActivity.songPosition == PlayerActivity.musicListPA.size - 1) {
            PlayerActivity.songPosition = 0
        } else {
            PlayerActivity.songPosition += 1
        }
    }
}

fun playPrevSong (){
    if(PlayerActivity.songPosition == 0 ){
        PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
    }
    else{
        PlayerActivity.songPosition -= 1
    }
}

fun closeApplication(){
    PlayerActivity.musicService?.stopForeground(true)
    PlayerActivity.musicService?.mediaPlayer?.release()
    PlayerActivity.musicService = null
    exitProcess(1)
}

fun toggleFavouriteButton() {
    if(PlayerActivity.musicListPA[PlayerActivity.songPosition].isFavourite){
        PlayerActivity.binding.favouritePlayerActivity.setImageResource(R.drawable.ic_favorite)
    }
    else{
        PlayerActivity.binding.favouritePlayerActivity.setImageResource(R.drawable.ic_add_to_fav)
    }
}

const val index = "index"
const val className = "class"
const val fromNowOn = "fromNowOn"