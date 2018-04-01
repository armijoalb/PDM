package com.armijoruiz.alberto.mykotlinapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.armijoruiz.alberto.mykotlinapp.Structures.Song
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomOnItemClickListener
import com.armijoruiz.alberto.mykotlinapp.other.*
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomOnItemClickListener {



    var recyclerView:RecyclerView?= null
    val mLayoutManager = LinearLayoutManager(this)
    var mAdapter: MyAdapter?=null
    var music_info : ArrayList<Song> = ArrayList()
    var currentPosition : Int = 0
    var playing_music = false
    var is_overlaying = false


    private var playcardbutton : ImageButton? = null
    private var playbutton : ImageButton? = null
    private var nextButton : ImageButton? = null
    private var prevButton : ImageButton? = null
    private var currentListening : TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pedimos permiso al usuario para acceder a la espacio del dispositivo.
        setupPermissions()
        playcardbutton = findViewById(R.id.playcardButton)
        playbutton = findViewById(R.id.playButton)
        nextButton = findViewById(R.id.nextButton)
        prevButton = findViewById(R.id.prevButton)
        currentListening = findViewById(R.id.songName)

        currentListening?.setText(music_info[currentPosition].name)



        sliding_panel.setOnClickListener {
            if (is_overlaying){
                Log.i("sliding_panel", "is_overlaying")
                playcardbutton?.visibility = View.INVISIBLE
            }else{
                Log.i("sliding_panel" ,"not overlaying")
                playcardbutton?.visibility = View.VISIBLE
            }

            is_overlaying = !is_overlaying
        }


        playcardbutton?.setOnClickListener {
            changePlayIcons()
            createMusicIntent(PLAYPAUSE)
        }

        playbutton?.setOnClickListener {
            changePlayIcons()
            Log.i("play_pause: ", "playpause llamado")
            createMusicIntent(PLAYPAUSE)
        }

        nextButton?.setOnClickListener {
            playcardbutton?.setImageResource(R.drawable.ic_pause_white)
            playbutton?.setImageResource(R.drawable.ic_pause)
            Log.i("play_pause: ", "play_next llamado")
            currentPosition = NextPosition(currentPosition,1)
            Log.i("nextButton:", "next_position: " + currentPosition )
            currentListening?.setText(music_info[currentPosition].name)
            createMusicIntent(NEXT,currentPosition)
        }

        prevButton?.setOnClickListener {
            playcardbutton?.setImageResource(R.drawable.ic_pause_white)
            playbutton?.setImageResource(R.drawable.ic_pause)
            Log.i("play_pause: ", "play_prev llamado")
            currentPosition = NextPosition(currentPosition,-1)
            Log.i("prevButton:", "next_position: " + currentPosition )
            currentListening?.setText(music_info[currentPosition].name)
            createMusicIntent(PREV,currentPosition)
        }

    }
    private fun NextPosition(currentPosition : Int,nextPosition:Int) : Int {
        var newPosition = (currentPosition+nextPosition+music_info.size)%music_info.size
        Log.i("current_position", currentPosition.toString())
        Log.i("next_position", newPosition.toString())

        return newPosition
    }

    private fun createMusicIntent(action:String, position: Int = currentPosition){
        var musicIntent = Intent(this, PlayMusicService::class.java)
        if(PlayMusicService.serviceStarted)
            musicIntent.setAction(action)
        else
            musicIntent.setAction(PLAYSONG)
        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        startService(musicIntent)
    }

    private fun changePlayIcons(){
        if(playing_music){
            playcardbutton?.setImageResource(R.drawable.ic_play_white)
            playbutton?.setImageResource(R.drawable.ic_play)
        }else{
            playcardbutton?.setImageResource(R.drawable.ic_pause_white)
            playbutton?.setImageResource(R.drawable.ic_pause)
        }

        playing_music = !playing_music
    }

    override fun onItemClick(position: Int) {
        playcardbutton?.setImageResource(R.drawable.ic_pause_white)
        playbutton?.setImageResource(R.drawable.ic_pause)
        currentPosition = position
        playing_music = true
        currentListening?.setText(music_info[position].name)
        createMusicIntent(PLAYSONG,position)
    }


    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        if(Build.VERSION.SDK_INT >= 23){
            if(permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                displayMusic()
            }
        }else{
            displayMusic()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMusic()
                } else {
                    finish()
                }
            }

        }
    }

    private fun displayMusic(){

        music_info = getMusic()

        recyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        recyclerView?.setHasFixedSize(true)

        recyclerView?.layoutManager = mLayoutManager

        mAdapter = MyAdapter(
                this,
                music_info,
                this)

        recyclerView?.adapter = mAdapter


    }

    private fun getMusic():ArrayList<Song>{
        val canciones = ArrayList<Song>()

        val contentResolver = contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val songTitle_id = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist_id = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songPath_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dur_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            var duration : Int
            var songTitle : String
            var songArtist : String
            var songPath : String


            do {
                duration = songCursor.getInt(dur_id) / 1000 // MediaStore.Audio.Media.Duration da los valores en ms.
                songTitle = songCursor.getString(songTitle_id)
                songArtist = songCursor.getString(songArtist_id)
                songPath = songCursor.getString(songPath_id)

                if(duration > 30)
                    canciones.add(Song(songArtist, songTitle, songPath, duration))

            }while(songCursor.moveToNext())
        }

        return canciones
    }


}
