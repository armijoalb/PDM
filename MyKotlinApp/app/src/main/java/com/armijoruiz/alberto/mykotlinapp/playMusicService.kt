package com.armijoruiz.alberto.mykotlinapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

/**
 * Created by Alberto on 09/03/2018.
 */

/**
 * Clase que crea un Servicio para reproducir música.
 *
 * Un Servicio es un tipo de clase que ejecuta una tarea de larga duración.
 */
class playMusicService : Service() {

    var currentPos:Int = 0
    var musicDataList:ArrayList<String> = ArrayList()
    var mMediaPlayer:MediaPlayer? = null

    override fun onBind(p0: Intent?): IBinder ?{
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // cogemos los datos que le hemos pasado al intent.
        currentPos = intent!!.getIntExtra(MyAdapter.MUSICITEMPOS,0)
        musicDataList = intent.getStringArrayListExtra(MyAdapter.MUSICLIST)

        // Comprobamos si ya está reproducciendo música.
        if( mMediaPlayer != null ){
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
            mMediaPlayer = null
        }

        // Creamos el objeto y preparamos la canción que vamos a reproducir.
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(musicDataList[currentPos])
        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}