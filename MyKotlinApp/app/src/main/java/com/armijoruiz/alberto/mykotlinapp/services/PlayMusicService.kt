package com.armijoruiz.alberto.mykotlinapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.armijoruiz.alberto.mykotlinapp.MainActivity
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.other.*


/**
 * Clase que crea un Servicio para reproducir música.
 *
 * Un Servicio es un tipo de clase que ejecuta una tarea de larga duración.
 */
class PlayMusicService : Service() {


    companion object {

        private var currentPos:Int? = null
        private var musicDataList:ArrayList<String> = ArrayList()
        private var mMediaPlayer:MediaPlayer? = null
        private var seekPosition : Int =  0
        private var mAudioManager: AudioManager? = null

        fun isMediaPlaying() = mMediaPlayer?.isPlaying
        var serviceStarted : Boolean = false
    }

    override fun onBind(p0: Intent?): IBinder ?{
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Buscamos la música.
        getMusic()

        serviceStarted = true

        // Creamos una instancia del audioManager.
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    override fun onDestroy() {
        super.onDestroy()

        musicDataList.clear()
        musicDataList = ArrayList()

        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null

        mAudioManager = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        when(intent!!.action){
            PLAYSONG -> {
                // cogemos los datos que le hemos pasado al intent.
                currentPos = intent.getIntExtra(MyAdapter.MUSICITEMPOS,0)
                playMusic(currentPos!!)
            }
            PLAYPAUSE -> {
                if(currentPos != null){
                    Log.i("onStartCommand: ", "playPause")
                    if(mMediaPlayer!!.isPlaying){
                        Log.i("play_pause: ", "pausando")
                        pauseMusic()
                    }else{
                        Log.i("play_pause: ", "resume")
                        resumeMusic()
                    }
                }else{
                    currentPos = intent.getIntExtra(MyAdapter.MUSICITEMPOS,0)
                    playMusic(currentPos!!)
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
            SET_PROGRESS ->{
                seekPosition = intent.getIntExtra(MainActivity.PROGRESS,0) * 1000
                resumeMusic()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun getMusic(){

        val contentResolver = contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(songUri, null, null, null,null)

        if(songCursor != null && songCursor.moveToFirst()){
            val songPath_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dura_id = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            var songPath : String
            var duration : Int


            do {

                songPath = songCursor.getString(songPath_id)
                duration = songCursor.getInt(dura_id) / 1000

                if(duration > 30)
                    musicDataList.add(songPath)

            }while(songCursor.moveToNext())
        }

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
            var newpos = NextPosition(currentPos!!,1)
            mMediaPlayer?.setDataSource(musicDataList[newpos])
            mMediaPlayer?.prepareAsync()
            Log.i("next song:", musicDataList[newpos])

        }

    }

    private fun NextPosition(currentPosition : Int,nextPosition:Int) : Int {
        var newPosition = (currentPosition+nextPosition+musicDataList.size)%musicDataList.size
        Log.i("current_position", currentPosition.toString())
        Log.i("next_position", newPosition.toString())

        return newPosition
    }

    private fun pauseMusic(){
        if(mMediaPlayer!!.isPlaying){
            seekPosition = mMediaPlayer!!.currentPosition
            mMediaPlayer!!.pause()
        }
    }

    private fun resumeMusic(){
        Log.i("mediaPlayer", "resumeMusic")
        mMediaPlayer!!.seekTo(seekPosition)
        mMediaPlayer!!.start()

    }

    private fun playNext(){
        var nextSong = NextPosition(currentPos!!,1)
        playMusic(nextSong)
    }

    private fun playPrev(){
        var nextSong = NextPosition(currentPos!!,-1)
        playMusic(nextSong)
    }

    private fun playMusic(pos:Int){
        Log.i("playMusic:", "attemping to play a song")
        Log.i("playMusic:", musicDataList[pos])
        setupMediaPlayer()
        mMediaPlayer?.reset()
        mMediaPlayer!!.setDataSource(musicDataList[pos])
        Log.i("data source:", musicDataList[pos])
        mMediaPlayer!!.prepare()
        currentPos = pos

    }
}