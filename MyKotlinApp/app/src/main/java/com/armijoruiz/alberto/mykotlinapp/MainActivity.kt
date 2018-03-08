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
import android.widget.ImageButton
import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        val fab = findViewById<FloatingActionButton>(R.id.boton_flotante)
        fab.setOnClickListener {
            myAsyncTask(this).execute()
        }

    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
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

        val recyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        recyclerView.setHasFixedSize(true)

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager

        val titles = ArrayList<String>()
        val authors = ArrayList<String>()
        for((title,author) in music_info){
            titles.add(title)
            authors.add(author)
        }
        val mAdapter = MyAdapter(this,titles.toList(),authors.toList())
        recyclerView.adapter = mAdapter

    }

    private fun getMusic():ArrayList<Pair<String,String> >{
        val canciones = ArrayList<Pair<String,String>>()

        val contentResolver = contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val songTitle_id = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist_id = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var songTitle : String
            var songArtist : String


            do {
                songTitle = songCursor.getString(songTitle_id)
                songArtist = songCursor.getString(songArtist_id)

                canciones.add(Pair(songTitle,songArtist))

            }while(songCursor.moveToNext())
        }

        return canciones
    }

    class myAsyncTask(context: Context) : AsyncTask<Void,Void,Int>(){

        var mContext:Context = context

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            Toast.makeText(mContext,"el resultado de la tarea es $result",Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg p0: Void?): Int {
            var resultado:Int = 0

            for(i:Int in 1..10)
                resultado = resultado+i

            return resultado
        }
    }


}
