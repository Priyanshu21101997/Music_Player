package com.example.musicplayer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FragmentNowPlayingBinding

class nowPlayingFragment : Fragment() {
    companion object{
        lateinit var binding: FragmentNowPlayingBinding
        const val SONG_NAME = "SONG_NAME"
        const val SONG_IMAGE = "SONG_IMAGE"

        fun newInstance(): nowPlayingFragment {
            val fragment = nowPlayingFragment()
//            val bundle = Bundle()
//            bundle.putString(SONG_NAME, title)
//            bundle.putString(SONG_IMAGE, artUri)
//            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)

//        val title = arguments?.getString(SONG_NAME)
//        val image = arguments?.getString(SONG_IMAGE)
//        Glide.with(requireContext())
//            .load(image)
//            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
//            .into(binding.nowOnSong)
//        binding.songName.text = title

        binding.root.visibility = View.INVISIBLE

        binding.playPauseBtn.setOnClickListener {
            if(PlayerActivity.isMediaPlaying){
                pauseMusic()
            }
            else{
                playMusic()
            }
        }

        binding.nextBtn.setOnClickListener {
            val nextIntent = Intent(requireContext(),NotificationBroadcast::class.java)
            nextIntent.action = ApplicationClass.NEXT
            activity?.sendBroadcast(nextIntent)
        }

        binding.root.setOnClickListener{
            val intent = Intent(context, PlayerActivity::class.java).setAction("pong")
            intent.putExtra(index,PlayerActivity.songPosition)
            intent.putExtra(className,"MusicAdapter")
            intent.putExtra(fromNowOn, true)
            activity?.startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.nowOnSong)
            binding.songName.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            Handler().postDelayed({
                binding.nowOnSong.isSelected = true},2000L)

        }

    }

    private fun pauseMusic(){
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        PlayerActivity.isMediaPlaying = false
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        PlayerActivity.musicService?.showNotification(R.drawable.ic_play)
        PlayerActivity.musicService?.mediaPlayer!!.pause()
    }

    private fun playMusic(){
        PlayerActivity.isMediaPlaying = true
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        PlayerActivity.musicService?.showNotification(R.drawable.ic_pause)
        PlayerActivity.musicService?.mediaPlayer!!.start()
        binding.playPauseBtn.setIconResource(R.drawable.ic_pause)

    }

}