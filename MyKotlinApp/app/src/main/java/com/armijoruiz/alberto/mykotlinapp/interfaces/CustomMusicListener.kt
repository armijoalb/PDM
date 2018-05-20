package com.armijoruiz.alberto.mykotlinapp.interfaces

interface CustomMusicListener {
    fun onUpdateProgress(progress:Int)
    fun onSongFinished(song_index:Int, song_name:String, duration:Int )
    fun onSongStateChanged(song_changed:Boolean)
    fun onSongAddedToFab(added:Boolean)
}