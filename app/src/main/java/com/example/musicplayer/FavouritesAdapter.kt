package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FavouriteViewBinding
import com.google.gson.Gson

class FavouritesAdapter(private val context: Context,private var favouritesList:ArrayList<Music>):
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {


    class ViewHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameFavourite
        val image = binding.songFavourite
        val root = binding.root

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.ViewHolder {
        return FavouritesAdapter.ViewHolder(
            FavouriteViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = favouritesList.size

    override fun onBindViewHolder(holder: FavouritesAdapter.ViewHolder, position: Int) {
        holder.title.text = favouritesList[position].title
        Glide.with(context)
            .load(favouritesList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
            sendIntent(position, "FavouritesAdapter")
        }
    }

        private fun sendIntent(position: Int, classNames: String) {

            var tallyIndex = -1
            MainActivity.musicListMA.forEachIndexed { index, music ->
                if(music.id == favouritesList[position].id){
                    tallyIndex = index
                }
            }

            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(index,position)
            intent.putExtra(className,classNames)
            intent.putExtra("tallyIndex",tallyIndex)
            context.startActivity(intent)
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicListWithFavourite(){
        val persistedFav = Gson().fromJson(MainActivity.sharedPreferences.getString("SHARED_PREF_DATA","{}"),SharedPrefData::class.java).favouriteChannels.sorted()
        favouritesList.clear()
        val newFav = ArrayList<Music>()
        persistedFav.forEach {
            MainActivity.musicListMA[it].isFavourite = true
            newFav.add(MainActivity.musicListMA[it])
        }
        favouritesList.addAll(newFav)
        notifyDataSetChanged()
    }

    }

