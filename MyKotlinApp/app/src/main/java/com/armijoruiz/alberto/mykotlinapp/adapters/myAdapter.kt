package com.armijoruiz.alberto.mykotlinapp.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.services.playMusicService
import com.armijoruiz.alberto.mykotlinapp.song
import kotlinx.android.synthetic.main.layout_holder.view.*

/**
 * Created by Alberto on 06/03/2018.
 */

class MyAdapter(context : Context,songs: ArrayList<song>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    val mContext = context
    val canciones = songs
    var musicList:ArrayList<String> = ArrayList()

    val PLAYSONG:String = "com.armijoruiz.alberto.mykotlinapp.action.PLAYSONG"
    val PLAYPAUSE:String = "com.armijoruiz.alberto.mykotlinapp.action.PLAYPAUSE"
    val NEXT:String = "com.armijoruiz.alberto.mykotlinapp.action.NEXT"
    val PREV:String = "com.armijoruiz.alberto.mykotlinapp.action.PREV"

    companion object {
        val MUSICLIST = "MusicList"
        val MUSICITEMPOS = "MusicItemPos"
    }

    class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        var mView = itemView
    }

    override fun getItemCount(): Int {
        return canciones.size
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        // Hay que crear un ViewHolder.
        val layout_inflater = LayoutInflater.from(parent?.context)
        val cellForRow = layout_inflater.inflate(R.layout.layout_holder,parent,false)
        return ViewHolder(cellForRow)
    }

    // Función para pausar o reanudar una canción.
    fun playPauseClick(){
        Log.i("play_pause: ", "playPause llamado")
        var musicIntent = Intent(mContext, playMusicService::class.java)
        musicIntent.setAction(PLAYPAUSE)
        musicIntent.putStringArrayListExtra(MUSICLIST, musicList)
        mContext.startService(musicIntent)
    }

    // Función para pasar a la siguiente canción.
    fun playNextSong(){
        Log.i("play_next: ", "playNextSong llamado")
        var musicIntent = Intent(mContext, playMusicService::class.java)
        musicIntent.setAction(NEXT)
        musicIntent.putStringArrayListExtra(MUSICLIST, musicList)
        mContext.startService(musicIntent)
    }

    // Función para pasar a la siguiente canción.
    fun playPrevSong(){
        Log.i("play_prev: ", "playPrevSong llamado")
        var musicIntent = Intent(mContext, playMusicService::class.java)
        musicIntent.setAction(PREV)
        musicIntent.putStringArrayListExtra(MUSICLIST, musicList)
        mContext.startService(musicIntent)
    }


    // Función que asocia un nuevo viewHolder a un elemento de los vectores.
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.itemView?.nombreMusica?.text = canciones[position].name
        holder?.itemView?.autor?.text = canciones[position].author

        // Creamos un onClickListener para el cardView.
        holder?.itemView?.setOnClickListener {
            for (i in 0 until canciones.size)
                musicList.add(canciones[i].path)

            Toast.makeText(mContext, "reproducciendo", Toast.LENGTH_SHORT).show()
            Log.i("musiclist", musicList.toString())
            var musicIntent = Intent(mContext, playMusicService::class.java)
            musicIntent.setAction(PLAYSONG)
            musicIntent.putStringArrayListExtra(MUSICLIST, musicList)
            musicIntent.putExtra(MUSICITEMPOS, position)
            mContext.startService(musicIntent)
        }

    }
}

