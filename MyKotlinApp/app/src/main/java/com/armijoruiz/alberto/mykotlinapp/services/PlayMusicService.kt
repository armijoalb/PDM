package com.armijoruiz.alberto.mykotlinapp.services

/**
 * Para la elaboración de este servicio y algunas clases relacionadas se ha tomado como ejemplo
 * este Github: @see <a href="https://github.com/SimpleMobileTools/Simple-Music-Player">
 *     https://github.com/SimpleMobileTools/Simple-Music-Player</a>
 */


import android.app.*
import android.content.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.armijoruiz.alberto.mykotlinapp.MainActivity
import com.armijoruiz.alberto.mykotlinapp.fragments.MusicFragment
import com.armijoruiz.alberto.mykotlinapp.R
import com.armijoruiz.alberto.mykotlinapp.structures.Song
import com.armijoruiz.alberto.mykotlinapp.adapters.MyAdapter
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomFabListener
import com.armijoruiz.alberto.mykotlinapp.interfaces.CustomMusicListener
import com.armijoruiz.alberto.mykotlinapp.other.*
import com.armijoruiz.alberto.mykotlinapp.receivers.NotificationControlsListener


/**
 * Clase que crea un Servicio para reproducir música.
 *
 * Un Servicio es un tipo de clase que ejecuta una tarea de larga duración.
 */
class PlayMusicService : Service() {
    private val TAG = PlayMusicService::class.java.simpleName


    companion object {

        private var currentPos:Int? = null
        private var musicDataList:ArrayList<Song>? = ArrayList()
        private var mMediaPlayer:MediaPlayer? = null
        private var seekPosition : Int =  0
        private var mAudioManager: AudioManager? = null
        private var mProgressBarHandler : Handler? = null
        private var CHANNEL_ID = "music_service_channel"
        private var NOTIFICATION_ID = 72
        private lateinit var notificationManager : NotificationManager
        private lateinit var uri_playlist : String
        private var default_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
        private var fab_list : ArrayList<String> = ArrayList()
        private var mStarted = false
        private lateinit var mediaSessions : MediaSessionCompat
        private lateinit var receiver : NotificationControlsListener
        private lateinit var mManager : MediaSessionManager
        private lateinit var mMediaController : MediaControllerCompat

        fun isMediaPlaying() = mMediaPlayer?.isPlaying
        fun isOreoOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        var serviceStarted : Boolean = false
        lateinit var customMusicListener: CustomMusicListener
        lateinit var customFabListener: CustomFabListener
        fun isInFabList(name:String):Boolean = fab_list.contains(name)
    }

    override fun onBind(p0: Intent?): IBinder ?{
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Buscamos la música.
        getFabList()

        serviceStarted = true

        // Creamos una instancia del audioManager.
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Creamos una instancia del handler.
        mProgressBarHandler = Handler()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        val filter: IntentFilter = IntentFilter()
        filter.addAction(NEXT)
        filter.addAction(PLAYPAUSE)
        filter.addAction(PREV)
        filter.addAction(ADD_REM)

        var mediaButtonReceiver : ComponentName = ComponentName(applicationContext,NotificationControlsListener::class.java)
        mediaSessions = MediaSessionCompat(this,"MusicService")





    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayingService()
    }

    private fun destroyPlayingService(){
        musicDataList?.clear()
        musicDataList = null

        mMediaPlayer?.stop()
        mMediaPlayer?.reset()
        mMediaPlayer?.release()
        mMediaPlayer = null

        mAudioManager = null
        mStarted = false

        handleProgressBar(false)
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID)

    }

    private fun getFabList(){
        fab_list = ArrayList()
        var uri : Uri = Uri.parse(MainActivity.fab_playlist)
        val contentR = contentResolver
        val cursor = contentR.query(uri,null, null, null, null)

        if(cursor != null && cursor.moveToNext()){
            do{
                var track_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID))
                var uri_content = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                var trackProjection = arrayOf( MediaStore.Audio.Media.TITLE)
                var selection = MediaStore.Audio.Media._ID + "=?"
                var selectionArgs = arrayOf(""+track_id)
                var mediaCursor = contentR.query(uri_content,trackProjection,selection,selectionArgs,null)
                if(mediaCursor != null){
                    if(mediaCursor.count >= 0){
                        mediaCursor.moveToFirst()
                        var title = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        fab_list.add(title)
                    }
                }
            }while (cursor.moveToNext())
        }

        Log.i(TAG,"fab_playlist size: " + fab_list.size)
        Log.i(TAG, fab_list.toString())

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("onStartCommand","Iniciando")
        when(intent!!.action){
            PLAYSONG -> {
                // cogemos los datos que le hemos pasado al intent.
                uri_playlist = intent.getStringExtra(MainActivity.default_uri_string)
                if(uri_playlist.isEmpty())
                    uri_playlist = default_uri
                musicDataList = getMusic()
                currentPos = intent.getIntExtra(MyAdapter.MUSICITEMPOS,0)
                playMusic(currentPos!!)
            }
            PLAYPAUSE -> {
                if(currentPos != null && mMediaPlayer != null){
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
                seekPosition = intent.getIntExtra(MusicFragment.PROGRESS,0) * 1000
                resumeMusic()
            }
            FINISH ->{
                Log.i("onStartCommand","finish called")
                customMusicListener.onUpdateProgress(0)
                destroyPlayingService()
            }
            ADD_FAB->{
                Log.i("onStartCommand", "adding to fab")
                MainActivity.addToPlaylist(musicDataList!![currentPos!!].id.toInt(),applicationContext)
                customMusicListener.onSongAddedToFab(true)
                customFabListener.onFabListChanged()
                getFabList()
                setupNotification()
            }
            REM_FAB->{
                Log.i("onStartCommand", "removing to fab")
                MainActivity.removeFromPlaylist(
                        MainActivity.getSongIdFromMediaStore(musicDataList!![currentPos!!].path,applicationContext).toInt(),
                        applicationContext)
                customMusicListener.onSongAddedToFab(false)
                customFabListener.onFabListChanged()
                getFabList()
                setupNotification()
            }
            ADD_REM->{
                if(isInFabList(musicDataList!![currentPos!!].name)){
                    MainActivity.removeFromPlaylist(
                            MainActivity.getSongIdFromMediaStore(musicDataList!![currentPos!!].path,applicationContext).toInt(),
                            applicationContext)
                    customMusicListener.onSongAddedToFab(false)
                    customFabListener.onFabListChanged()
                }else{
                    MainActivity.addToPlaylist(musicDataList!![currentPos!!].id.toInt(),applicationContext)
                    customMusicListener.onSongAddedToFab(true)
                    customFabListener.onFabListChanged()

                }
                getFabList()
                setupNotification()
            }
            else-> Log.i("onStartCommand", "not reconised action")
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun getMusic():ArrayList<Song>{
        val canciones = ArrayList<Song>()
        Log.i(TAG,"taking music")
        val contentResolver = contentResolver
        //val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songUri = Uri.parse(uri_playlist)
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
            var audioID : Long


            do {
                duration = songCursor.getInt(dur_id) / 1000 // MediaStore.Audio.Media.Duration da los valores en ms.
                songTitle = songCursor.getString(songTitle_id)
                songArtist = songCursor.getString(songArtist_id)
                songPath = songCursor.getString(songPath_id)
                audioID = songCursor.getLong(song_id)

                // Solo metemos aquellas "canciones" que sean más de 30 segs, para evitar mostrar tonos.
                if(duration > 30)
                    canciones.add(Song(songArtist, songTitle, songPath, duration,audioID))

            }while(songCursor.moveToNext())
        }
        Log.i(TAG,"finished "+ canciones.size)
        return canciones
    }

    private fun setupMediaPlayer(){

        // Comprobamos si ya está reproducciendo música.
        if( mMediaPlayer != null )
            return

        mMediaPlayer = MediaPlayer()
        musicDataList = getMusic()


        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
            handleProgressBar(isMediaPlaying()!!)
            Log.i("prepared","setting up notification")
            setupNotification()
            Log.i("prepared: ", "playing song")
        }

        mMediaPlayer?.setOnCompletionListener {
            mMediaPlayer?.reset()
            var newpos = NextPosition(currentPos!!,1)
            currentPos = newpos
            mMediaPlayer?.setDataSource(musicDataList!![newpos].path)
            mMediaPlayer?.prepareAsync()
            Log.i("next song:", musicDataList!![newpos].name)
            customMusicListener.onSongFinished(newpos,musicDataList!![newpos].name, musicDataList!![newpos].duration)
            setupNotification()
        }

    }


    private fun setupNotification(){

        val author = musicDataList!![currentPos!!].author
        val song_name = musicDataList!![currentPos!!].name
        val playPauseIcon = if(isMediaPlaying()!!) R.drawable.ic_pause else R.drawable.ic_play
        val fabButton = if(isInFabList(song_name)) R.drawable.ic_full_star else R.drawable.ic_emtpy_star


        // Necesario para que se muestren notificaciones a partir de android oreo o superior.
        if(isOreoOrHigher()){
            Log.i("setupNotification", "is oreo")
            val name = resources.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(this)
            }
        }


        Log.i(TAG, mediaSessions.sessionToken.toString())
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(song_name)
                .setContentText(author)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setShowWhen(isMediaPlaying()!!)
                .setWhen(System.currentTimeMillis() - mMediaPlayer!!.currentPosition)
                .setOngoing(isMediaPlaying()!!)
                .setUsesChronometer(isMediaPlaying()!!)
                .setContentIntent(getContentIntent())
                .addAction(R.drawable.ic_prev,"PREVIOUS", getActionIntent(PREV) ) // 0
                .addAction(playPauseIcon, "PLAYPAUSE", getActionIntent(PLAYPAUSE)) // 1
                .addAction(R.drawable.ic_next, "NEXT", getActionIntent(NEXT)) // 2
                .addAction(fabButton, "ADD_REM", getActionIntent(ADD_REM))
                .setStyle(MediaStyle()
                    .setMediaSession(mediaSessions.sessionToken)
                    .setShowActionsInCompactView(0,1,2))


        if(isOreoOrHigher()){
            notificationBuilder.setColorized(true)
        }

        val notification = notificationBuilder.build();


        Log.i("setupNotification", "starting notification")


        if(!mStarted && isMediaPlaying()!!){
            startForeground(NOTIFICATION_ID, notification)
            notificationManager.notify(NOTIFICATION_ID,notification)
            mStarted = true
        }else{
            if(!isMediaPlaying()!!) {
                Log.i("setupNotification","media is not playing")
                stopForeground(false)
            }
        }

        notificationManager.notify(NOTIFICATION_ID,notification)

        Log.i("setupNofication", "started")
    }

    private fun getActionIntent(action:String):PendingIntent{
        val intent = Intent(this, NotificationControlsListener::class.java)
        intent.setAction(action)
        return PendingIntent.getBroadcast(this, 0,intent,0)
    }

    private fun getContentIntent():PendingIntent{
        val contentIntent = Intent(this, MusicFragment::class.java)
        return PendingIntent.getActivity(this,0,contentIntent,0)
    }

    private fun handleProgressBar(is_playing : Boolean){
        if(is_playing){
            mProgressBarHandler!!.post(object: Runnable{
                override fun run() {
                    if (mMediaPlayer != null){
                        val current_progress : Int = mMediaPlayer!!.currentPosition
                        customMusicListener.onUpdateProgress(current_progress/1000)
                        mProgressBarHandler!!.removeCallbacks(null)
                        mProgressBarHandler!!.postDelayed(this, 1000)
                    }
                }
            })
        }else{
            mProgressBarHandler!!.removeCallbacks(null)
        }
    }

    private fun NextPosition(currentPosition : Int,nextPosition:Int) : Int {
        var newPosition = (currentPosition+nextPosition+musicDataList!!.size)%musicDataList!!.size
        Log.i("current_position", currentPosition.toString())
        Log.i("next_position", newPosition.toString())

        return newPosition
    }

    private fun pauseMusic(){
        if(mMediaPlayer!!.isPlaying){
            seekPosition = mMediaPlayer!!.currentPosition
            mMediaPlayer!!.pause()
            handleProgressBar(false)
            customMusicListener.onSongStateChanged(false)
            setupNotification()
        }
    }

    private fun resumeMusic(){
        Log.i("mediaPlayer", "resumeMusic")
        mMediaPlayer!!.seekTo(seekPosition)
        mMediaPlayer!!.start()
        handleProgressBar(true)
        customMusicListener.onSongStateChanged(true)
        setupNotification()

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
        setupMediaPlayer()
        mMediaPlayer?.reset()
        mMediaPlayer!!.setDataSource(musicDataList!![pos].path)
        Log.i("data source:", musicDataList!![pos].path)
        mMediaPlayer!!.prepare()
        currentPos = pos
        Log.i("playMusic:","prepared")
        customMusicListener.onSongStateChanged(true)
        customMusicListener.onSongFinished(currentPos!!,musicDataList!![currentPos!!].name, musicDataList!![currentPos!!].duration)
        Log.i(TAG, fab_list.toString())
        Log.i(TAG, musicDataList!![currentPos!!].name)
        customMusicListener.onSongAddedToFab(fab_list.contains(musicDataList!![currentPos!!].name))
    }
}

