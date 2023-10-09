package com.example.musicplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson


const val REQ_CODE = 13
const val SHARE_REQ_CODE = 14

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener{

    companion object{
        lateinit var musicListPA: ArrayList<Music>
        var songPosition = 0
        var tallyIndex = -1
        var isMediaPlaying = false
        var musicService: MusicService? = null
        lateinit var binding: ActivityPlayerBinding
        var repeat = false
        var min15Option: Boolean = false
        var min30Option: Boolean = false
        var min60Option: Boolean = false
//        var isServiceRunning = false


    }


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // For starting service
//        LocalBroadcastManager.getInstance(this).registerReceiver(ServiceEchoResponse(),
//            IntentFilter("pong")
//        )

//            if(!isServiceRunning) {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
//            }
//        else{
//                musicService!!.seekBarSetup()
//                musicService!!.showNotification(R.drawable.ic_pause)
//            }


        initialiseLayout()


        binding.playPauseBtn.setOnClickListener {
            if(isMediaPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }

        binding.nextBtn.setOnClickListener {
            playNextSong()
            modifyPlayerAndNotification()
        }

        binding.prevBtn.setOnClickListener {
            playPrevSong()
            modifyPlayerAndNotification()
        }

        binding.favouritePlayerActivity.setOnClickListener {
            musicListPA[songPosition].isFavourite = !musicListPA[songPosition].isFavourite


            val favListFromPref = Gson().fromJson(MainActivity.sharedPreferences.getString("SHARED_PREF_DATA","{}"),SharedPrefData::class.java).favouriteChannels.sorted()
            val index = favListFromPref.indexOf(if(tallyIndex == -1)songPosition else tallyIndex)
            val newList = favListFromPref.toMutableList()
            if(index == -1){
                newList.add(if(tallyIndex == -1)songPosition else tallyIndex)
            }
            else{
                newList.removeAt(index)
            }
            Log.d("RE_LIFE", "${newList.toString()} ")
            val myEdit = MainActivity.sharedPreferences.edit()
            myEdit.putString("SHARED_PREF_DATA",Gson().toJson(SharedPrefData(newList)))
            myEdit.apply()
//            setFavouriteButton(songPosition, musicListPA)
            toggleFavouriteButton()

        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    musicService?.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.repeatBtn.setOnClickListener {
            if(!repeat){
                repeat = true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
            else{
                repeat = false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            }
        }

        binding.backBtnPA.setOnClickListener {
            onBackPressed()
        }

        binding.equalizerBtn.setOnClickListener {
            try {
                binding.equalizerBtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
                val intentEq = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intentEq.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intentEq.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService?.mediaPlayer?.audioSessionId);
                intentEq.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(intentEq, REQ_CODE)
            }
            catch (e: Exception){
                Toast.makeText(this,"Equaliser Not Supported",Toast.LENGTH_LONG).show()
                binding.equalizerBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            }
        }

        binding.timerBtn.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.shareBtn.setOnClickListener {
            binding.shareBtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            val shareIntent = Intent()
            with(shareIntent){
                this.action = Intent.ACTION_SEND
                this.type = "audio/*"
                this.putExtra(Intent.EXTRA_STREAM,Uri.parse(musicListPA[songPosition].path))
                startActivityForResult(Intent.createChooser(this,"Sharing Music File"),
                    SHARE_REQ_CODE)
            }
        }

    }


    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(binding.songImage)

        binding.textView.text = musicListPA[songPosition].title
//        setFavouriteButton(songPosition = songPosition, respectiveList)
        toggleFavouriteButton()
        if(repeat){
            binding.repeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
        }
    }

    private fun createMediaPlayer(){
        try {
            if (musicService?.mediaPlayer == null) {
                musicService?.mediaPlayer = MediaPlayer()
            }
            musicService?.mediaPlayer!!.reset()
            musicService?.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService?.mediaPlayer!!.prepare()
            musicService?.mediaPlayer!!.start()
            isMediaPlaying = true

            if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {

                binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
                binding.startTime.text =
                    formatDuration(musicService?.mediaPlayer?.currentPosition!!.toLong())
                binding.endTime.text =
                    formatDuration(musicService?.mediaPlayer?.duration!!.toLong())
                binding.seekBar.progress = 0
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration
            }

            // Listner for song completion
            musicService?.mediaPlayer?.setOnCompletionListener(this)
        }
        catch (e: Exception){
            return
        }
    }

     private fun pauseMusic(){
        isMediaPlaying = false
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
         musicService?.showNotification(R.drawable.ic_play)
         nowPlayingFragment.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        musicService?.mediaPlayer!!.pause()
    }

     private fun playMusic(){
        isMediaPlaying = true
        binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
         musicService?.showNotification(R.drawable.ic_pause)
         nowPlayingFragment.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
         musicService?.mediaPlayer!!.start()

    }

    private fun initialiseLayout(){
        songPosition = intent.getIntExtra(index,0)
        tallyIndex = intent.getIntExtra("tallyIndex",-1)
        val className = intent.getStringExtra(className)

        when(className){
            "MusicAdapterSearch" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                setLayout()
            }
            "FavouritesAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.favouritesList)
                setLayout()
            }
            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListMA)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }



    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        musicService!!.showNotification(R.drawable.ic_pause)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    private fun modifyPlayerAndNotification(){
        if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            setLayout()
        }
        musicService?.showNotification(R.drawable.ic_pause)
        createMediaPlayer()
        if(!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && !supportFragmentManager.isDestroyed){
            val f = nowPlayingFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, f ,MainActivity.NOWONFRAGMENT)
                .commit()
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playNextSong()
        modifyPlayerAndNotification()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQ_CODE && resultCode == Activity.RESULT_OK){
            binding.equalizerBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            return
        }
        if(requestCode == SHARE_REQ_CODE){
            binding.shareBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showBottomSheetDialog(){
        binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
        var dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()

        val min15 = dialog.findViewById<LinearLayout>(R.id.mins15)
        val min30 = dialog.findViewById<LinearLayout>(R.id.mins30)
        var min60 = dialog.findViewById<LinearLayout>(R.id.mins60)
//
        min15?.setOnClickListener {
            min15Option = true
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            Thread{Thread.sleep(15* 60000)
            if(min15Option){
                closeApplication()
            }}.start()
            dialog.dismiss()
        }

        min30?.setOnClickListener {
            min30Option = true
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            Thread{Thread.sleep(30* 60000)
                if(min15Option){
                    closeApplication()
                }}.start()
            dialog.dismiss()
        }

        min60?.setOnClickListener {
            min60Option = true
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.black))
            Thread{Thread.sleep(60* 60000)
                if(min15Option){
                    closeApplication()
                }}.start()
            dialog.dismiss()
        }

        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener{
            override fun onDismiss(dialogInterface: DialogInterface?) {
                binding.timerBtn.setColorFilter(ContextCompat.getColor(this@PlayerActivity,R.color.black))
                dialogInterface?.dismiss()
            }
        })
    }

    @SuppressLint("CommitPrefEdits")
    override fun onResume() {
        super.onResume()


//        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(this,MusicService::class.java).setAction("ping"))

    }

    override fun onDestroy() {
        super.onDestroy()
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(ServiceEchoResponse())
//        isServiceStarted = false
    }

//    class ServiceEchoResponse(): BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if(intent?.action == "pong" && intent.getBooleanExtra(fromNowOn,false))
//            isServiceRunning = true
//        }

//    }

}