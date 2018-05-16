package com.armijoruiz.alberto.mykotlinapp.structures


/**
 * Created by Alberto on 09/03/2018.
 */
class Song(aut:String,song_name:String, song_path : String, dur:Int=0, song_id: Long) {
    val author : String = aut
    val name :String = song_name
    val path : String = song_path
    val duration : Int = dur
    val id : Long = song_id
}