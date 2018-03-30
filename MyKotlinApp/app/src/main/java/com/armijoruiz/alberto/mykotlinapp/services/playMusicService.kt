package com.armijoruiz.alberto.mykotlinapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter

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
    var mAudioManager: AudioManager? = null

    val PLAYSONG:String = "com.armijoruiz.alberto.mykotlinapp.action.PLAYSONG"
    val PLAYPAUSE:String = "com.armijoruiz.alberto.mykotlinapp.action.PLAYPAUSE"
    val NEXT:String = "com.armijoruiz.alberto.mykotlinapp.action.NEXT"
    val PREV:String = "com.armijoruiz.alberto.mykotlinapp.action.PREV"

    override fun onBind(p0: Intent?): IBinder ?{
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Creamos una instancia del audioManager.
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    override fun onDestroy() {
        super.onDestroy()

        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null

        mAudioManager = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        musicDataList = intent!!.getStringArrayListExtra(MyAdapter.MUSICLIST)

        when(intent.action){
            PLAYSONG -> {
                // cogemos los datos que le hemos pasado al intent.
                currentPos = intent.getIntExtra(MyAdapter.MUSICITEMPOS,0)
                playMusic(currentPos)
            }
            PLAYPAUSE -> {
                Log.i("onStartCommand: ", "playPause")
                if(mMediaPlayer!!.isPlaying){
                    Log.i("play_pause: ", "pausando")
                    pauseMusic()
                }else{
                    Log.i("play_pause: ", "resume")
                    resumeMusic()
                }
            }
            NEXT -> {
                // Llamamos a PlayNext.
                playNext()
            }
            PREV -> {
                // Llamamos a PlayPrev.
                playPrev()
            }
        }


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
            Log.i("hi", "hello")
            mMediaPlayer!!.seekTo(seekPosition)
            mMediaPlayer!!.start()
        }
    }

    private fun playNext(){
        var nextSong = NextPosition(currentPos,1)
        playMusic(nextSong)
    }

    private fun playPrev(){
        var nextSong = NextPosition(currentPos,-1)
        playMusic(nextSong)
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