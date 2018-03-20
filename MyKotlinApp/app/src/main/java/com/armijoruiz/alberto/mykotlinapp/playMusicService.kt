package com.armijoruiz.alberto.mykotlinapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log

/**
 * Clase que crea un Servicio para reproducir música.
 *
 * Un Servicio es un tipo de clase que ejecuta una tarea de larga duración.
 */
class playMusicService : Service() {

    var currentPos:Int = 0
    var musicDataList:ArrayList<String> = ArrayList()
    var mMediaPlayer:MediaPlayer? = null
    var seekPosition : Int =  0

    override fun onBind(p0: Intent?): IBinder ?{
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // cogemos los datos que le hemos pasado al intent.
        currentPos = intent!!.getIntExtra(MyAdapter.MUSICITEMPOS,0)
        musicDataList = intent.getStringArrayListExtra(MyAdapter.MUSICLIST)


        playMusic(currentPos)


        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupMediaPlayer(){

        // Comprobamos si ya está reproducciendo música.
        if( mMediaPlayer != null )
            return

        mMediaPlayer = MediaPlayer()

        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
            Log.i("prepared: ", "playing song")
        }

        mMediaPlayer?.setOnCompletionListener {
            mMediaPlayer?.reset()
            var newpos = NextPosition(currentPos,1)
            mMediaPlayer?.setDataSource(musicDataList[newpos])
            mMediaPlayer?.prepareAsync()
            Log.i("next song:", musicDataList[newpos])

        }

    }

    private fun NextPosition(currentPosition : Int,nextPosition:Int) : Int {
        var newPosition = (currentPosition+nextPosition+musicDataList.size)%musicDataList.size

        return newPosition
    }

    private fun pauseMusic(){
        if(mMediaPlayer!!.isPlaying){
            seekPosition = mMediaPlayer!!.currentPosition
            mMediaPlayer!!.pause()
        }
    }

    private fun resumeMusic(){
        if(!mMediaPlayer!!.isPlaying){
            mMediaPlayer!!.seekTo(seekPosition)
            mMediaPlayer!!.prepare()
        }
    }

    private fun playMusic(pos:Int){
        Log.i("playMusic:", "attemping to play a song")
        setupMediaPlayer()
        mMediaPlayer?.reset()
        mMediaPlayer!!.setDataSource(musicDataList[pos])
        Log.i("data source:", musicDataList[pos])
        mMediaPlayer!!.prepare()

    }
}