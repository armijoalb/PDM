package com.armijoruiz.alberto.mykotlinapp.fragments

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
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.structures.Song
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomMusicListener
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomOnItemClickListener
import com.armijoruiz.alberto.mykotlinapp.other.*
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState

class   MusicFragment : Fragment(), CustomOnItemClickListener, CustomMusicListener
{
    var recyclerView:RecyclerView?= null
    val mLayoutManager = LinearLayoutManager(context)
    var mAdapter: MyAdapter?=null
    var music_info : ArrayList<Song> = ArrayList()
    var currentPosition : Int = 0
    var playing_music = false


    private var playcardbutton : ImageButton? = null
    private var playbutton : ImageButton? = null
    private var nextButton : ImageButton? = null
    private var prevButton : ImageButton? = null
    private var currentListening : TextView? = null
    private var slidepanel : SlidingUpPanelLayout? = null
    private var seekBar : SeekBar? = null
    private var maxDuration : TextView? = null
    private var currentDuration : TextView? = null
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

        PlayMusicService.customMusicListener = this

        // Obtenemos los botones y elementos de la pantalla.
        //playcardbutton = mView.findViewById(R.id.playcardButton)
        //playbutton = mView.findViewById(R.id.playButton)
        //nextButton = mView.findViewById(R.id.nextButton)
        //prevButton = mView.findViewById(R.id.prevButton)
        //currentListening = mView.findViewById(R.id.songName)
        //seekBar = mView.findViewById(R.id.seekBar)
        //maxDuration = mView.findViewById(R.id.totalDuration)
        //currentDuration = mView.findViewById(R.id.currentDuration)



        // Agregamos onClickListener.
        setupButtons()

        // Agregamos funcionalidad al slideUpPanel.
        setupSlidePanel()

        return mView
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "killing app")

        if(PlayMusicService.serviceStarted)
            createMusicIntent(FINISH,currentPosition)

        activity.stopService(Intent(activity,PlayMusicService::class.java))
    }

    override fun onResume() {
        super.onResume()
        Log.i("on_resume", "set on resume" + " is media playing :"+ playing_music)
        currentPosition = Config.current_position
        if(music_info.size > 0)
            currentListening?.text = music_info[currentPosition].name
        playing_music = Config.is_media_playing
        changePlayIcons()

        mAdapter?.updateList(getMusic())
    }

    override fun onPause() {
        super.onPause()
        Log.i("on_pause","set on pause" + " is media playing: " + playing_music)
        Config.current_position = currentPosition
        Config.is_media_playing = playing_music
        Log.i("on_pause", "finish")
    }

    // Función para agregar funcionalidad al Sliding up panel.
    private fun setupSlidePanel(){
        slidepanel?.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener{

            // Función que se llama cuando se hace slide con el panel.
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            // Función que se llama cuando el panel cambia de estado.
            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                Log.i("slideuppanel", "sliding panel state changed")
                Log.i("slideuppanel", "newstate: "+newState)

                when(newState){
                    PanelState.EXPANDED ->{
                        playcardbutton?.visibility = View.INVISIBLE
                    }
                    PanelState.COLLAPSED ->{
                        playcardbutton?.visibility = View.VISIBLE
                    }
                    PanelState.DRAGGING ->{
                        if(previousState == PanelState.EXPANDED)
                            playcardbutton?.visibility = View.VISIBLE
                        else
                            playcardbutton?.visibility = View.INVISIBLE
                    }
                }
            }

        })
    }


    // Función que crea los listener de los botones, seekBar, etc...
    private fun setupButtons(){
        if(music_info.size > 0) {
            currentListening?.setText(music_info[currentPosition].name)
            setSeekBarParams(music_info[currentPosition].duration)
        }

        playcardbutton?.setOnClickListener {
            playing_music = !playing_music
            changePlayIcons()
            createMusicIntent(PLAYPAUSE)

        }

        playbutton?.setOnClickListener {
            playing_music = !playing_music
            changePlayIcons()
            Log.i("play_pause: ", "playpause llamado")
            createMusicIntent(PLAYPAUSE)

        }

        nextButton?.setOnClickListener {
            Log.i("play_pause: ", "play_next llamado")
            currentPosition = NextPosition(currentPosition,1)
            Log.i("nextButton:", "next_position: " + currentPosition )
            currentListening?.setText(music_info[currentPosition].name)
            setSeekBarParams(music_info[currentPosition].duration)
            createMusicIntent(NEXT,currentPosition)

        }

        prevButton?.setOnClickListener {
            Log.i("play_pause: ", "play_prev llamado")
            currentPosition = NextPosition(currentPosition,-1)
            Log.i("prevButton:", "next_position: " + currentPosition )
            currentListening?.setText(music_info[currentPosition].name)
            setSeekBarParams(music_info[currentPosition].duration)
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

    // función para actualizar el estado de la barra y el tiempo actual
    override fun onUpdateProgress(progress: Int) {
        seekBar?.progress = progress
        currentDuration?.text = MyAdapter.getFormattedTime(progress/60,progress%60)
    }

    // función para cambiar el nombre de la canción y la información de esta.
    override fun onSongFinished(song_index : Int) {
        currentPosition = song_index
        currentListening?.text = music_info[currentPosition].name
        setSeekBarParams(music_info[currentPosition].duration)
        Config.current_position = currentPosition
    }

    override fun onSongStateChanged(song_changed:Boolean) {
        playing_music = song_changed
        Config.is_media_playing = song_changed
        changePlayIcons()
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

    // Función que calcula la siguiente posición.
    private fun NextPosition(currentPosition : Int,nextPosition:Int) : Int {
        var newPosition = (currentPosition+nextPosition+music_info.size)%music_info.size
        Log.i("current_position", currentPosition.toString())
        Log.i("next_position", newPosition.toString())

        return newPosition
    }

    private fun createMusicIntent(action:String, position: Int = currentPosition, seek_pos : Int = 0){
        var musicIntent = Intent(context, PlayMusicService::class.java)
        if(PlayMusicService.serviceStarted)
            musicIntent.setAction(action)
        else
            musicIntent.setAction(PLAYSONG)

        musicIntent.putExtra(MyAdapter.MUSICITEMPOS,position)
        musicIntent.putExtra(PROGRESS, seek_pos)
        activity.startService(musicIntent)
    }

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

    // Implementación de la interfaz para onClickItem del RecyclerView.
    override fun onItemClick(position: Int) {
        currentPosition = position
        currentListening?.setText(music_info[position].name)
        setSeekBarParams(music_info[currentPosition].duration)
        createMusicIntent(PLAYSONG,position)
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayMusic()
                } else {

                }
            }

        }
    }

    // Función para mostrar la música encontrada.
    private fun displayMusic(){

        music_info = getMusic()

        // Agregamos onClickListener.
        setupButtons()



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

