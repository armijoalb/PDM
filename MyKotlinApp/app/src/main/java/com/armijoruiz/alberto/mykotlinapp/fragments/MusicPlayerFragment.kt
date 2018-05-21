package com.armijoruiz.alberto.mykotlinapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.armijoruiz.alberto.mykotlinapp.MainActivity
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomMusicListener
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomMusicPlayerConnector
import com.armijoruiz.alberto.mykotlinapp.other.*
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class MusicPlayerFragment : Fragment(), CustomMusicListener, CustomMusicPlayerConnector {

    private var playcardbutton : ImageButton? = null
    private var playbutton : ImageButton? = null
    private var nextButton : ImageButton? = null
    private var prevButton : ImageButton? = null
    private var currentListening : TextView? = null
    private var slidepanel : SlidingUpPanelLayout? = null
    private var starButton : ImageButton? = null
    private var seekBar : SeekBar? = null
    private var maxDuration : TextView? = null
    private var currentDuration : TextView? = null
    var currentPosition : Int = 0
    var playing_music = false
    private val TAG = MusicPlayerFragment::class.java.simpleName

    companion object {
        fun newInstance() =
                MusicPlayerFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        currentPosition = Config.current_position
        playing_music = Config.is_media_playing
        changePlayIcons()
    }

    override fun onPause() {
        super.onPause()
        Config.current_position = currentPosition
        Config.is_media_playing = playing_music
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.i(TAG, "oncreateViews")
        var mView:View = layoutInflater.inflate(R.layout.fragment_music,container,false)
        playcardbutton = mView.findViewById(R.id.playcardButton)
        playbutton = mView.findViewById(R.id.playButton)
        nextButton = mView.findViewById(R.id.nextButton)
        prevButton = mView.findViewById(R.id.prevButton)
        currentListening = mView.findViewById(R.id.songName)
        seekBar = mView.findViewById(R.id.seekBar)
        starButton = mView.findViewById(R.id.starButton)
        maxDuration = mView.findViewById(R.id.totalDuration)
        currentDuration = mView.findViewById(R.id.currentDuration)

        PlayMusicService.customMusicListener = this

        // Agregamos onClickListener.
        setupButtons()

        return mView
    }

    override fun onPanelStateChanged(visibility: Boolean) {
        if(!visibility){
            playcardbutton?.visibility = View.INVISIBLE
        }else{
            playcardbutton?.visibility = View.VISIBLE
        }
    }

    /**
     * Funciones de la interfaz CustomMusicListener.
     */
    // función para actualizar el estado de la barra y el tiempo actual
    override fun onUpdateProgress(progress: Int) {
        seekBar?.progress = progress
        currentDuration?.text = MyAdapter.getFormattedTime(progress/60,progress%60)
    }

    // función para cambiar el nombre de la canción y la información de esta.
    override fun onSongFinished(song_index : Int, song_name:String, duration: Int) {
        currentPosition = song_index
        currentListening?.text = song_name
        setSeekBarParams(duration)
        Config.current_position = currentPosition
    }

    override fun onSongStateChanged(song_changed:Boolean) {
        playing_music = song_changed
        Config.is_media_playing = song_changed
        changePlayIcons()
    }

    override fun onSongAddedToFab(added: Boolean) {
        Log.i(TAG,"changing icon")
        Log.i(TAG,"added to fab"+added.toString())
        if(added){
            starButton?.setImageResource(R.drawable.ic_full_star)
        }else{
            starButton?.setImageResource(R.drawable.ic_emtpy_star)
        }

    }

    // Función que crea los listener de los botones, seekBar, etc...
    private fun setupButtons(){
        playcardbutton?.setOnClickListener {
            playing_music = !playing_music
            changePlayIcons()
            createMusicIntent(PLAYPAUSE)

        }

        starButton?.setOnClickListener{
            if(PlayMusicService.isMediaPlaying()!!){
                var musicIntent = Intent(context, PlayMusicService::class.java)
                if(PlayMusicService.isInFabList(currentListening?.text.toString())){
                    musicIntent.setAction(REM_FAB)
                }else{
                    musicIntent.setAction(ADD_FAB)
                }
                activity.startService(musicIntent)
            }
        }

        playbutton?.setOnClickListener {
            playing_music = !playing_music
            changePlayIcons()
            Log.i("play_pause: ", "playpause llamado")
            createMusicIntent(PLAYPAUSE)
        }

        nextButton?.setOnClickListener {
            Log.i("play_pause: ", "play_next llamado")
            Log.i("nextButton:", "next_position: " + currentPosition )
            createMusicIntent(NEXT,currentPosition)

        }

        prevButton?.setOnClickListener {
            Log.i("play_pause: ", "play_prev llamado")
            Log.i("prevButton:", "next_position: " + currentPosition )
            createMusicIntent(PREV,currentPosition)
        }

        seekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                // DO NOTHING.
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // DO NOTHING.
            }

            override fun onStopTrackingTouch(seekbar: SeekBar) {
                // Obtenemos el progreso actual.
                Log.i("seekBar", "new progress"+ seekbar.progress)
                var act_progress:Int = seekbar.progress
                playing_music = true
                changePlayIcons()
                createMusicIntent(SET_PROGRESS,currentPosition,act_progress)
            }
        })


    }

    // Función para establecer los parámetros del seekbar.
    private fun setSeekBarParams(max : Int, current: Int = 0){
        seekBar?.progress = current
        seekBar?.max = max

        val max_secs:Int = max % 60
        val max_min:Int = max / 60
        val current_secs:Int = current%60
        val current_min:Int = current/60
        maxDuration?.text = MyAdapter.getFormattedTime(max_min, max_secs)
        currentDuration?.text = MyAdapter.getFormattedTime(current_min,current_secs)
    }

    // Función para cambiar los iconos de pausa o reproducir.
    private fun changePlayIcons(){
        if(!playing_music){
            Log.i("changePlayButtons: ", "it's not playing")
            playcardbutton?.setImageResource(R.drawable.ic_play_white)
            playbutton?.setImageResource(R.drawable.ic_play)
        }else{
            Log.i("changePlayButtons: ", "it's playing")
            playcardbutton?.setImageResource(R.drawable.ic_pause_white)
            playbutton?.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun createMusicIntent(action:String, position: Int = currentPosition, seek_pos : Int = 0){
        var musicIntent = Intent(context, PlayMusicService::class.java)
        if(PlayMusicService.serviceStarted)
            musicIntent.setAction(action)
        else {
            musicIntent.setAction(PLAYSONG)
            musicIntent.putExtra(MainActivity.default_uri_string,MainActivity.default_playlist)
        }

        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        musicIntent.putExtra(MusicFragment.PROGRESS, seek_pos)
        activity.startService(musicIntent)
    }
}
