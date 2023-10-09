package com.example.musicplayer

import android.os.Parcelable

data class SharedPrefData(
    val favouriteChannels :List<Int> = listOf(),
  ) {
}