package com.armijoruiz.alberto.mykotlinapp

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_holder.view.*

/**
 * Created by Alberto on 06/03/2018.
 */
class MyAdapter(context : Context,songs: ArrayList<song>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    val mContext = context
    val canciones = songs
    var musicList:ArrayList<String> = ArrayList()

    companion object {
        val MUSICLIST = "MusicList"
        val MUSICITEMPOS = "MusicItemPos"
    }

    class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        var mView = itemView
        var playbutton = itemView.playButton

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

    // Función que asocia un nuevo viewHolder a un elemento de los vectores.
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.itemView?.nombreMusica?.text = canciones[position].name
        holder?.itemView?.autor?.text = canciones[position].author

        holder?.itemView?.playButton?.setOnClickListener{
            for(i in 0 until canciones.size)
                musicList.add(canciones[i].path)

            Toast.makeText(mContext,"reproducciendo",Toast.LENGTH_SHORT).show()
            Log.i("musiclist",musicList.toString())
            var musicIntent = Intent(mContext,playMusicService::class.java)
            musicIntent.putStringArrayListExtra(MUSICLIST,musicList)
            musicIntent.putExtra(MUSICITEMPOS,position)
            mContext.startService(musicIntent)
        }



    }
}

