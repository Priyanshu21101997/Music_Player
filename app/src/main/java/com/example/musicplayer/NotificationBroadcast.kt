package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action){
            ApplicationClass.PREV -> {
                playPrevSong()
                setNowOnLayout(context)
                setLayoutAndCreateMP(context)
                PlayerActivity.musicService?.showNotification(R.drawable.ic_pause)

            }
            ApplicationClass.NEXT -> {
                playNextSong()
                setNowOnLayout(context)
                setLayoutAndCreateMP(context)
                PlayerActivity.musicService?.showNotification(R.drawable.ic_pause)
            }
            ApplicationClass.PLAY -> {
                if(PlayerActivity.isMediaPlaying){
                    PlayerActivity.isMediaPlaying = false
                    PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
                    PlayerActivity.musicService?.showNotification(R.drawable.ic_play)
                    nowPlayingFragment.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
                    PlayerActivity.musicService?.mediaPlayer!!.pause()
                }
                else{
                    PlayerActivity.isMediaPlaying = true
                    PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
                    PlayerActivity.musicService?.showNotification(R.drawable.ic_pause)
                    nowPlayingFragment.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
                    PlayerActivity.musicService?.mediaPlayer!!.start()
                }
            }
            ApplicationClass.EXIT -> {
                closeApplication()
            }
        }
    }

    private fun setLayoutAndCreateMP(context: Context?) {
        Glide.with(context!!)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(PlayerActivity.binding.songImage)

        PlayerActivity.binding.textView.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        toggleFavouriteButton()

        PlayerActivity.musicService?.mediaPlayer!!.reset()
        PlayerActivity.musicService?.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        PlayerActivity.musicService?.mediaPlayer!!.prepare()
        PlayerActivity.musicService?.mediaPlayer!!.start()
        PlayerActivity.isMediaPlaying = true
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        PlayerActivity.binding.startTime.text = formatDuration(PlayerActivity.musicService?.mediaPlayer?.currentPosition!!.toLong())
        PlayerActivity.binding.endTime.text = formatDuration(PlayerActivity.musicService?.mediaPlayer?.duration!!.toLong())
        PlayerActivity.binding.seekBar.progress = 0
        PlayerActivity.binding.seekBar.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
    }

    private fun setNowOnLayout(context: Context?) {
        Glide.with(context!!)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(nowPlayingFragment.binding.nowOnSong)
        nowPlayingFragment.binding.songName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
    }

}