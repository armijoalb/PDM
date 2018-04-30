package com.armijoruiz.alberto.mykotlinapp.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomOnItemClickListener
import com.armijoruiz.alberto.mykotlinapp.structures.Song
import kotlinx.android.synthetic.main.layout_holder.view.*

/**
 * Created by Alberto on 06/03/2018.
 */

class  MyAdapter(context : Context, songs: ArrayList<Song>, val itemlistener : CustomOnItemClickListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    val mContext = context
    var canciones = songs

    var listener : CustomOnItemClickListener? = null

    companion object {
        val MUSICITEMPOS = "MusicItemPos"
        fun getFormattedTime(min:Int,sec:Int) = String.format("%d:%02d",min,sec)
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


    // Función que asocia un nuevo viewHolder a un elemento de los vectores.
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        listener = itemlistener
        holder?.itemView?.nombreMusica?.text = canciones[position].name
        val minutes:Int = canciones[position].duration / 60
        val sec:Int = canciones[position].duration % 60
        holder?.itemView?.duration?.text = getFormattedTime(minutes,sec)

        // Creamos un onClickListener para el cardView.
        holder?.itemView?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(listener != null)
                    listener?.onItemClick(holder.adapterPosition)
            }
        })

    }

    fun updateList(list:ArrayList<Song>){
        canciones = list
        notifyDataSetChanged()
    }
}

