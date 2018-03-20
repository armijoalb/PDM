package com.armijoruiz.alberto.mykotlinapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.widget.Toast
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log

class MainActivity : AppCompatActivity() {

    var recyclerView:RecyclerView?= null
    val mLayoutManager = LinearLayoutManager(this)
    var mAdapter:MyAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pedimos permiso al usuario para acceder a la espacio del dispositivo.
        setupPermissions()

        val fab = findViewById<FloatingActionButton>(R.id.boton_flotante)
        fab.setOnClickListener{
            mAdapter!!.playPauseClick()
        }
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

        val music_info = getMusic()

        recyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        recyclerView?.setHasFixedSize(true)

        recyclerView?.layoutManager = mLayoutManager

        mAdapter = MyAdapter(this,music_info)
        recyclerView?.adapter = mAdapter


    }

    private fun getMusic():ArrayList<song>{
        val canciones = ArrayList<song>()

        val contentResolver = contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val songTitle_id = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist_id = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songPath_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var songTitle : String
            var songArtist : String
            var songPath : String


            do {
                songTitle = songCursor.getString(songTitle_id)
                songArtist = songCursor.getString(songArtist_id)
                songPath = songCursor.getString(songPath_id)

                canciones.add(song(songArtist,songTitle,songPath))

            }while(songCursor.moveToNext())
        }

        return canciones
    }


}
