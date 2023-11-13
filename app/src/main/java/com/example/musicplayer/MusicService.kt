package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MusicService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
//        LocalBroadcastManager
//            .getInstance(this)
//            .registerReceiver(ServiceEchoReceiver(), IntentFilter("ping"))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }

    fun showNotification(resId: Int) {
        val mediaSession = MediaSessionCompat(this, "MY Music")
//        lateinit var bitmap:Bitmap

        val prevIntent = Intent(this,NotificationBroadcast::class.java).setAction(ApplicationClass.PREV)
        val prevPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this,NotificationBroadcast::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this,NotificationBroadcast::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this,0,playIntent, PendingIntent.FLAG_IMMUTABLE)

        val exitIntent = Intent(this,NotificationBroadcast::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(this,0,exitIntent, PendingIntent.FLAG_IMMUTABLE)

//        val imageArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
//        if(imageArt != null){
//            bitmap = BitmapFactory.decodeByteArray(imageArt,0, imageArt.size)
//        }
//        else{
//            bitmap = BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_splash_screen)
//        }

        val notification =  NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_playlist)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setLargeIcon(bitmap)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous, "Prev", prevPendingIntent)
            .addAction(resId, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
            .setProgress(100,0,false)
            .build()

        startForeground(13, notification)
    }

    fun seekBarSetup(){

        handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                PlayerActivity.binding.startTime.text = formatDuration(PlayerActivity.musicService?.mediaPlayer?.currentPosition!!.toLong())
                PlayerActivity.binding.seekBar.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this,200)
            }
        })
    }

//    override fun onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(ServiceEchoReceiver())
//        super.onDestroy()
//    }

//    class ServiceEchoReceiver() : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if(intent?.action == "ping")
//            LocalBroadcastManager.getInstance(context!!).sendBroadcast(Intent("pong"))
//        }
//
//    }
}