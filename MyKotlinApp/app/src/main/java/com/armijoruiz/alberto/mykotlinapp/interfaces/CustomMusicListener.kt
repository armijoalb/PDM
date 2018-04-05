package com.armijoruiz.alberto.mykotlinapp.interfaces

interface CustomMusicListener {
    fun onUpdateProgress(progress:Int)
    fun onSongFinished(song_index:Int)
    fun onSongStateChanged(song_changed:Boolean)
}