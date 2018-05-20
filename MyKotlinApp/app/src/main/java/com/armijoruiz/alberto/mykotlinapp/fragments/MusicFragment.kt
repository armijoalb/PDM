package com.armijoruiz.alberto.mykotlinapp.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.content.Intent
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.armijoruiz.alberto.mykotlinapp.MainActivity
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.structures.Song
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomOnItemClickListener
import com.armijoruiz.alberto.mykotlinapp.other.*
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService


class   MusicFragment : Fragment(), CustomOnItemClickListener
{
    var recyclerView:RecyclerView?= null
    val mLayoutManager = LinearLayoutManager(context)
    var mAdapter: MyAdapter?=null
    var music_info : ArrayList<Song> = ArrayList()
    var currentPosition : Int = 0

    private var TAG : String = MusicFragment::class.java.simpleName
    private var canReadStorage :Boolean = false

    companion object {
        val PROGRESS = "progress"
        @JvmStatic
        fun newInstance(canRead:Boolean) =
                MusicFragment().apply {
                    arguments = Bundle().apply {
                        canReadStorage = canRead
                    }
                }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?) : View?{
        super.onCreate(savedInstanceState)
        var mView : View = layoutInflater.inflate(R.layout.activity_main,container,false)

        Log.i(TAG,"oncreateView")

        recyclerView = mView.findViewById(R.id.mRecyclerView)

        if(canReadStorage)
            displayMusic()

        return mView
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "killing app")
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.updateList(getMusic())
    }

    private fun createMusicIntent(action:String, position: Int = currentPosition,  uri:String = MainActivity.default_playlist,seek_pos : Int = 0){
    var musicIntent = Intent(context, PlayMusicService::class.java)
        if(PlayMusicService.serviceStarted)
            musicIntent.setAction(action)
        else
            musicIntent.setAction(PLAYSONG)

        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        musicIntent.putExtra(PROGRESS, seek_pos)
        musicIntent.putExtra(MainActivity.default_uri_string,uri)
        activity.startService(musicIntent)
    }



    // Implementación de la interfaz para onClickItem del RecyclerView.
    override fun onItemClick(position: Int) {
        currentPosition = position
        createMusicIntent(PLAYSONG,position)
    }


    // Función para mostrar la música encontrada.
    private fun displayMusic(){
        music_info = getMusic()

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = mLayoutManager

        mAdapter = MyAdapter(
                music_info,
                this)

        recyclerView?.adapter = mAdapter

    }

    // Función para encontrar las canciones dentro del dispositivo.
    private fun getMusic():ArrayList<Song>{
        val canciones = ArrayList<Song>()

        val contentResolver = context.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val songTitle_id = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist_id = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songPath_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dur_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val song_id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var duration : Int
            var songTitle : String
            var songArtist : String
            var songPath : String
            var songID : Long


            do {
                duration = songCursor.getInt(dur_id) / 1000 // MediaStore.Audio.Media.Duration da los valores en ms.
                songTitle = songCursor.getString(songTitle_id)
                songArtist = songCursor.getString(songArtist_id)
                songPath = songCursor.getString(songPath_id)
                songID = songCursor.getLong(song_id)

                // Solo metemos aquellas "canciones" que sean más de 30 segs, para evitar mostrar tonos.
                if(duration > 30)
                    canciones.add(Song(songArtist, songTitle, songPath, duration,songID))

            }while(songCursor.moveToNext())
        }

        return canciones
    }


}

