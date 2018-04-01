package com.armijoruiz.alberto.mykotlinapp.Structures


/**
 * Created by Alberto on 09/03/2018.
 */
class Song(aut:String,song_name:String, song_path : String, dur:Int=0) {
    val author : String = aut
    val name :String = song_name
    val path : String = song_path
    val duration : Int = dur
}