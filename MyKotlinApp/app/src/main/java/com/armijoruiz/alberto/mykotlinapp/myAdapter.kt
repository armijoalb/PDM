package com.armijoruiz.alberto.mykotlinapp

import android.content.Context
import android.support.v7.widget.RecyclerView
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
class MyAdapter(context : Context, tit:List<String> , auth:List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    val mContext = context
    val titulos = tit
    val autores = auth

    class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        val mView = itemView
        val playbutton = itemView.playButton
    }

    override fun getItemCount(): Int {
        return titulos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
       // Hay que crear un ViewHolder.
        val layout_inflater = LayoutInflater.from(parent?.context)
        val cellForRow = layout_inflater.inflate(R.layout.layout_holder,parent,false)
        return ViewHolder(cellForRow)
    }

    // Función que asocia un nuevo viewHolder a un elemento de los vectores.
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.itemView?.nombreMusica?.text = titulos[position]
        holder?.itemView?.autor?.text = autores[position]

        holder?.playbutton?.setOnClickListener{
            Toast.makeText(mContext,"reproducciendo", Toast.LENGTH_LONG).show()
        }
    }
}

